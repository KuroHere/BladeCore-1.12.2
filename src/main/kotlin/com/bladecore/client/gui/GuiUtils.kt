package com.bladecore.client.gui

import com.bladecore.client.event.EventBus
import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.impl.clickgui.ClickGuiHud
import com.bladecore.client.gui.impl.hudeditor.HudEditorGui
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen

object GuiUtils {
    private val mc = Minecraft.getMinecraft()

    var clickGuiNew: ClickGuiHud? = null
    var hudEditorGui: HudEditorGui? = null

    fun bootstrap() {
        clickGuiNew = ClickGuiHud()
        clickGuiNew?.onRegister()

        hudEditorGui = HudEditorGui()
        hudEditorGui?.onRegister()
    }

    fun showGui(gui: GuiScreen) {
        mc.displayGuiScreen(gui)
        if (gui is AbstractGui) {
            gui.onGuiOpen()
            gui.isActive = true
            EventBus.subscribe(gui)
        }
    }

    fun hideAll() {
        if (mc.currentScreen is AbstractGui){
            (mc.currentScreen as AbstractGui).onGuiCloseAttempt()
            return
        }

        mc.displayGuiScreen(null)
    }
}