package com.bladecore.client.gui.impl.clickgui.elements.settings.impl

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.gui.impl.clickgui.elements.ModuleButton
import com.bladecore.client.gui.impl.clickgui.elements.settings.SettingButton
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.setting.type.StringSetting
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import org.lwjgl.input.Keyboard

class StringButton(val setting: StringSetting, gui: AbstractGui, baseButton: ModuleButton) : SettingButton(gui, baseButton) {
    override fun onRegister() {}
    override fun onGuiCloseAttempt() {}
    override fun onTick() {}

    override fun onInvisible() = cancelTyping()
    override fun onGuiOpen() = cancelTyping()
    override fun onGuiClose() = cancelTyping()
    override fun onSettingsOpen() = cancelTyping()
    override fun onSettingsClose() = cancelTyping()

    override fun isVisible() = setting.isVisible

    private var typing = false
    private var typed = ""

    override fun onRender() {
        super.onRender()
        val text = if (typing) "${setting.name}: $typed" else setting.name
        fr.drawString(text, pos.plus(ClickGui.space, height / 2.0), scale = ClickGui.settingFontSize)
        if (!typing) fr.drawString(setting.value, pos.plus(width - fr.getStringWidth(setting.value, ClickGui.settingFontSize) - ClickGui.space, height / 2.0), scale = ClickGui.settingFontSize)
    }

    override fun onMouseAction(action: MouseAction, button: Int) {
        if (action != MouseAction.CLICK || !hovered) return
        baseButton.settings.filterIsInstance<StringButton>().forEach { it.applyTyped() }
        baseButton.settings.filterIsInstance<DoubleSlider>().forEach { it.applyTyped() }
        typing = true
        typed = setting.value
    }

    override fun onKey(typedChar: Char, key: Int) {
        when (key) {
            Keyboard.KEY_ESCAPE -> {
                cancelTyping()
            }

            Keyboard.KEY_RETURN -> {
                applyTyped()
            }

            Keyboard.KEY_BACK -> { typed = typed.dropLast(1) }

            else -> {
                if (!typedChar.isLetter()) return
                typed += typedChar
            }
        }
    }

    fun applyTyped() {
        if (!typing) return
        setting.value = typed
        setting.listeners.forEach { it() }
        cancelTyping()
    }

    private fun cancelTyping() {
        typing = false
    }
}