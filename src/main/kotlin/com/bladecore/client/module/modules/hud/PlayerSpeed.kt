package com.bladecore.client.module.modules.hud

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.MathUtils.roundToPlaces
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getHeight
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import com.bladecore.client.utils.render.font.Fonts
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import kotlin.math.hypot

object PlayerSpeed: DraggableHudModule(
    "PlayerSpeed",
    "Shows your speed"
){
    private val size by setting("Size", 1.0, 0.5, 3.0, 0.05)
    private val averageTicks by setting("Average ticks", 5.0, 1.0, 50.0, 1.0)
    private val places by setting("Round Places", 1.0, 1.0, 5.0, 1.0)

    private var speedList = arrayListOf(0.0)

    private const val margin = 4.0

    override fun onRender() {
        val c1 = HUD.getColor(0)
        val c2 = Color(230, 230, 230)

        Fonts.DEFAULT.drawString(text1, Vec2d(pos.x + margin * size / 2.0, pos.y + getHeight() / 2.0), true, c2, size)
        Fonts.DEFAULT.drawString(text2, Vec2d(pos.x + margin * size / 2.0 + Fonts.DEFAULT.getStringWidth(text1, size), pos.y + getHeight() / 2.0), true, c1, size)
    }

    override fun getWidth() = Fonts.DEFAULT.getStringWidth(text1, size) + Fonts.DEFAULT.getStringWidth(text2, size) + margin * size
    override fun getHeight() = Fonts.DEFAULT.getHeight(size) + margin * size

    private val text1 get() =
        (speedList.takeLast(averageTicks.toInt()).sum() / averageTicks).roundToPlaces(places.toInt()).toString()

    private const val text2 = " BPS"

    init {
        for (i in 0..50) {
            speedList.add(0.0)
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.END) return@safeListener

            val speed = hypot(player.posX - player.prevPosX, player.posZ - player.prevPosZ) * 20.0
            speedList.add(speed)
            speedList.dropWhile { speedList.count() > averageTicks }
        }
    }
}