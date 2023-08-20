package com.bladecore.client.module.modules.client

import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.render.ColorUtils
import com.bladecore.client.utils.render.ColorUtils.b
import com.bladecore.client.utils.render.ColorUtils.g
import com.bladecore.client.utils.render.ColorUtils.r
import com.bladecore.client.utils.render.ColorUtils.setAlphaD
import java.awt.Color
import kotlin.math.ceil

object HUD : Module(
    "HUD",
    "Global configuration for hud",
    Category.CLIENT,
    enabledByDefault = true
) {
    private val color1 by setting("Color 1", Color(30, 190, 240))
    private val color2 by setting("Color 2", Color(170, 30, 215))
    val bgColor by setting("Background Color", Color(25, 25, 25))


    fun getColor(offset: Int = 0, b: Double = 1.0): Color {
        return getRainbow(offset * 200, b)
    }

    fun getColorByProgress(progress: Double, brightness: Double = 1.0): Color {
        val c = ColorUtils.lerp(color1.setAlphaD(1.0), color2.setAlphaD(1.0), progress)
        val b = clamp(brightness, 0.0, 1.0).toFloat()
        return Color(c.r * b, c.g * b, c.b * b, 1f)
    }

    private fun getRainbow(timeOffset: Int, brightness: Double): Color {
        var time = ceil((System.currentTimeMillis() - timeOffset) / 20.0)
        time %= 360.0
        val progressRaw = (time / 360.0f).toFloat()
        val progress = (if (progressRaw > 0.5) 1f - progressRaw else progressRaw) * 2.0
        return getColorByProgress(progress, brightness)
    }
}