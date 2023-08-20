package com.bladecore.client.module.modules.hud

import com.bladecore.client.event.listener.runSafeR
import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.FPSCounter
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getHeight
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import com.bladecore.client.utils.render.font.Fonts
import com.bladecore.client.utils.threads.loop.DelayedLoopThread
import java.awt.Color

object FPS : DraggableHudModule(
    "FPS",
    "Shows your fps"
) {
    private val size by setting("Size", 1.0, 0.5, 3.0, 0.05)
    private val averageTime by setting("Average time", 500.0, 100.0, 5000.0, 100.0)

    private const val margin = 4.0
    private const val text1 = "FPS: "
    private var text2 = "0"

    private var fpsList = ArrayList<Pair<Double, Long>>()

    private val tickThread = DelayedLoopThread("FPS Counter Thread", { isEnabled() }, { 25L }) {
        runSafeR {
            val fps = 1.0 / FPSCounter.deltaTime
            fpsList.add(fps to System.currentTimeMillis())
            fpsList.removeIf { System.currentTimeMillis() - it.second > averageTime }

            val sum = fpsList.sumOf { it.first }
            val count = fpsList.count()

            if (count != 0) text2 = (sum / count.toDouble()).toInt().toString()
        } ?: run {
            try {
                fpsList.clear()
                Thread.sleep(1000L)
            } catch (_: InterruptedException) { }
        }
    }

    init {
        tickThread.reload()
    }

    override fun onEnable() {
        tickThread.interrupt()
    }

    override fun onRender() {
        val c1 =  HUD.getColor(0)
        val c2 = Color(230, 230, 230)

        Fonts.DEFAULT.drawString(text1, Vec2d(pos.x + margin * size / 2.0, pos.y + getHeight() / 2.0), true, c1, size)
        Fonts.DEFAULT.drawString(text2, Vec2d(pos.x + margin * size / 2.0 + Fonts.DEFAULT.getStringWidth(text1, size), pos.y + getHeight() / 2.0), true, c2, size)
    }

    override fun getWidth() = Fonts.DEFAULT.getStringWidth(text1, size) + Fonts.DEFAULT.getStringWidth(text2, size) + margin * size
    override fun getHeight() = Fonts.DEFAULT.getHeight(size) + margin * size
}