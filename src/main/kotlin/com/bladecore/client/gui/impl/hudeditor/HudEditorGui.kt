package com.bladecore.client.gui.impl.hudeditor

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.module.DraggableHudModule

class HudEditorGui : AbstractGui() {
    override fun onMouseAction(action: MouseAction, button: Int) {
        ModuleManager.getModules().filterIsInstance<DraggableHudModule>().forEach {
            it.handleMouseAction(mouse, action, button)
        }
    }
}