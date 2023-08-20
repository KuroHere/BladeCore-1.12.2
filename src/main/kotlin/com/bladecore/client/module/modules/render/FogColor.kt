package com.bladecore.client.module.modules.render

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.render.ColorUtils.b
import com.bladecore.client.utils.render.ColorUtils.g
import com.bladecore.client.utils.render.ColorUtils.r
import net.minecraftforge.client.event.EntityViewRenderEvent
import java.awt.Color

object FogColor : Module(
    "FogColor",
    "Changes fog color",
    Category.RENDER
) {
    private val colorMode by setting("Color Mode", ColorMode.Custom)
    private val color by setting("Color", Color(130, 130, 230), { colorMode == ColorMode.Custom })

    private enum class ColorMode {
        Custom,
        Client
    }

    init {
        safeListener<EntityViewRenderEvent.FogColors> {
            when (colorMode) {
                ColorMode.Custom -> {
                    it.red = color.r
                    it.green = color.g
                    it.blue = color.b
                }
                ColorMode.Client -> {
                    val color = HUD.getColor(0, 0.7)
                    it.red = color.red.toFloat() / 255f
                    it.green = color.green.toFloat() / 255f
                    it.blue = color.blue.toFloat() / 255f
                }
            }
        }
    }
}