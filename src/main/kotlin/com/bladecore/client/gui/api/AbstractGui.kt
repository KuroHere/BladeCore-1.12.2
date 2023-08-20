package com.bladecore.client.gui.api

import com.bladecore.client.event.EventBus
import com.bladecore.client.event.events.Render2DEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.gui.api.other.IGuiElement
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.GLUtils
import com.bladecore.client.utils.render.Screen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.gameevent.TickEvent

abstract class AbstractGui : GuiScreen(), IGuiElement {
    var isActive = false
    protected val mc: Minecraft = Minecraft.getMinecraft()
    var mouse = Vec2d(0.0, 0.0)

    final override fun doesGuiPauseGame(): Boolean { return false }
    open fun getScaleFactor(): Double { return 1.0 }

    override fun onRegister() {}
    override fun onGuiOpen() {}
    override fun onGuiClose() {}

    override fun onTick() {}
    override fun onRender() {}

    override fun onMouseAction(action: MouseAction, button: Int) {}
    override fun onKey(typedChar: Char, key: Int) {}


    final override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        mouse = getMousePos(Vec2d(mouseX, mouseY))
    }

    final override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        onMouseAction(MouseAction.CLICK, mouseButton)
    }

    final override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)

        onMouseAction(MouseAction.RELEASE, -1)
    }

    final override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == 1) onGuiCloseAttempt()
        onKey(typedChar, keyCode)
    }

    final override fun onGuiClosed() {
        super.onGuiClosed()
        isActive = false
        onGuiClose()
        EventBus.unsubscribe(this)
    }

    override fun onGuiCloseAttempt() {
        mc.displayGuiScreen(null)
        if (mc.currentScreen == null) mc.setIngameFocus()
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (!isActive) return@safeListener
            onTick()
        }

        safeListener<Render2DEvent> {
            if (!isActive) return@safeListener
            GLUtils.withScale(getScaleFactor()) {
                onRender()
            }
        }
    }

    private fun getMousePos(prev: Vec2d): Vec2d {
        val x = prev.x / width.toDouble()
        val y = prev.y / height.toDouble()
        val scale = getScaleFactor() * 2.0

        return Vec2d(Screen.width * x / scale, Screen.height * y / scale)
    }
}