package com.bladecore.client.gui.impl.clickgui.elements.settings.impl

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.gui.impl.clickgui.elements.ModuleButton
import com.bladecore.client.gui.impl.clickgui.elements.settings.SettingButton
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.setting.type.UnitSetting
import com.bladecore.client.utils.render.font.FontUtils.drawString

class UnitButton(val setting: UnitSetting, gui: AbstractGui, baseButton: ModuleButton) : SettingButton(gui, baseButton) {
    override fun onRegister() {}
    override fun onGuiOpen() {}
    override fun onGuiClose() {}
    override fun onGuiCloseAttempt() {}
    override fun onTick() {}
    override fun onKey(typedChar: Char, key: Int) {}

    override fun isVisible() = setting.isVisible

    override fun onRender() {
        super.onRender()
        fr.drawString(setting.name, pos.plus(ClickGui.space, height / 2.0), scale = ClickGui.settingFontSize)
    }

    override fun onMouseAction(action: MouseAction, button: Int) {
        if (action != MouseAction.CLICK || button != 0 || !hovered) return
        setting.invokeBlock()
    }
}