package com.bladecore.client.module.modules.client

import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.gui.GuiUtils
import com.bladecore.client.gui.impl.hudeditor.HudEditorGui
import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.Module
import net.minecraftforge.fml.common.gameevent.TickEvent

object HudEditor : Module(
    "HudEditor",
    "?$#(@H*",
    Category.CLIENT,
    alwaysListenable = true
){
    init {
        listener<ConnectionEvent.Connect> {
            setEnabled(false)
        }

        listener<TickEvent.ClientTickEvent> {
            if(mc.currentScreen !is HudEditorGui) setEnabled(false)
        }
    }

    override fun onEnable() {
        GuiUtils.hideAll()
        GuiUtils.hudEditorGui?.let { GuiUtils.showGui(it) }
    }

    override fun onDisable() {
        if (mc.currentScreen is HudEditorGui) GuiUtils.hideAll()
        ModuleManager.getModules().filterIsInstance<DraggableHudModule>().forEach { it.isDragging = false }
    }
}