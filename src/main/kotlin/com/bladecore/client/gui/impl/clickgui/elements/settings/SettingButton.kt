package com.bladecore.client.gui.impl.clickgui.elements.settings

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.elements.InteractiveElement
import com.bladecore.client.gui.impl.clickgui.elements.ModuleButton
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.utils.math.Vec2d

abstract class SettingButton(gui: AbstractGui, val baseButton: ModuleButton) : InteractiveElement(Vec2d.ZERO, 0.0, 0.0, gui) {
    var index = 0
    open fun onSettingsOpen() {}
    open fun onSettingsClose() {}
    override fun onRender() {}

    open fun onInvisible() {}

    open fun isVisible() = true
    open fun getSettingHeight() = ClickGui.height
}