package com.bladecore.client.gui.impl.clickgui.elements.settings.impl

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.gui.impl.clickgui.elements.ModuleButton
import com.bladecore.client.gui.impl.clickgui.elements.settings.SettingButton
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.setting.type.DoubleSetting
import com.bladecore.client.utils.extension.transformIf
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.MathUtils.decimalPlaces
import com.bladecore.client.utils.math.MathUtils.lerp
import com.bladecore.client.utils.math.MathUtils.roundToPlaces
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.GLUtils
import com.bladecore.client.utils.render.RenderUtils2D
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import org.lwjgl.input.Keyboard
import java.awt.Color
import kotlin.math.abs
import kotlin.math.round

class DoubleSlider(val setting: DoubleSetting, gui: AbstractGui, baseButton: ModuleButton) : SettingButton(gui, baseButton) {
    override fun onRegister() {}
    override fun onGuiClose() {}
    override fun onGuiCloseAttempt() {}
    override fun onTick() {}

    override fun onGuiOpen() = reset()
    override fun onSettingsOpen() = reset()
    override fun onSettingsClose() = reset()
    override fun onInvisible() = reset()
    override fun isVisible() = setting.isVisible

    private var renderProgress = 0.0
    private var sliding = false
    var typing = false
    private var typed = ""

    private val formattedName get() =
        setting.value.toString().transformIf(setting.step % 1 == 0.0) { it.dropLast(2) }

    override fun onRender() {
        super.onRender()

        if (!isVisible()) reset().also { return }
        if (!hovered || typing) sliding = false

        val startX = pos.x + ClickGui.space
        val endX = pos.x + width - ClickGui.space

        val sliderStartX = pos.x
        val sliderEndX = pos.x + width

        val mouseProgress = (gui.mouse.x - sliderStartX) / (sliderEndX - sliderStartX)

        if (sliding) {
            val rawValue = lerp(setting.min, setting.max, mouseProgress)
            var valueRounded = round(rawValue / setting.step) * setting.step
            valueRounded = valueRounded.roundToPlaces(decimalPlaces(setting.step))
            if (abs(valueRounded) == 0.0) valueRounded = 0.0
            setting.value = clamp(valueRounded, setting.min, setting.max)
            setting.listeners.forEach { it() }
        }

        val centerY = pos.y + height / 2.0

        val renderProgressTo = clamp((setting.value - setting.min) / (setting.max - setting.min), 0.0, 1.0)
        renderProgress = lerp(renderProgress, renderProgressTo, GLUtils.deltaTimeDouble() * 3.0)

        val sliderBegin = Vec2d(sliderStartX, centerY)
        val sliderEnd = Vec2d(lerp(sliderStartX, sliderEndX, renderProgress), centerY)
        val sliderFull = Vec2d(sliderEndX, centerY)

        if (typing) {
            val text = "${setting.name}: $typed"
            fr.drawString(text, Vec2d(startX, centerY), scale = ClickGui.settingFontSize)
            return
        } else {
            if (sliding) {
                RenderUtils2D.drawLine(
                    sliderBegin,
                    sliderFull,
                    2.0f,
                    Color(90, 90, 90)
                )

                RenderUtils2D.drawLine(
                    sliderBegin,
                    sliderEnd,
                    2.0f,
                    Color.WHITE
                )
            }

            val text1 = setting.name
            val text2 = formattedName

            fr.drawString(text1, Vec2d(startX, centerY), scale = ClickGui.settingFontSize)
            fr.drawString(text2, Vec2d(endX - fr.getStringWidth(text2, ClickGui.settingFontSize), centerY), scale = ClickGui.settingFontSize)

        }
    }

    override fun onMouseAction(action: MouseAction, button: Int) {
        if (action == MouseAction.CLICK && button == 1 && hovered) {
            baseButton.settings.filterIsInstance<StringButton>().forEach { it.applyTyped() }
            baseButton.settings.filterIsInstance<DoubleSlider>().forEach { it.applyTyped() }
            typing = true
            typed = formattedName
        }

        sliding = action == MouseAction.CLICK && button == 0 && hovered && !typing

        if (!hovered && action == MouseAction.CLICK) cancelTyping()
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
                if (!"1234567890,.-".toCharArray().contains(typedChar)) return
                typed += typedChar
            }
        }
    }

    private fun reset() {
        sliding = false
        cancelTyping()
    }

    fun applyTyped() {
        if (!typing) return
        typed.toDoubleOrNull()?.let {
            var valueRounded = round(it / setting.step) * setting.step
            valueRounded = valueRounded.roundToPlaces(decimalPlaces(setting.step))
            if (abs(valueRounded) == 0.0) valueRounded = 0.0
            setting.value = clamp(valueRounded, setting.min, setting.max)

            setting.listeners.forEach { it() }
        }
        cancelTyping()
    }

    private fun cancelTyping() {
        typing = false
        typed = setting.value.toString()
    }
}