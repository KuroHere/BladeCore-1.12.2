package com.bladecore.client.module.modules.hud

import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.render.ColorUtils.setAlpha
import com.bladecore.client.utils.render.RenderUtils2D
import com.bladecore.client.utils.render.shader.RectBuilder

object RenderTest : DraggableHudModule(
    "RenderTest",
    "sus"
) {
    private val huy by setting("Huy", false)
    private val alpha1 by setting("Alpha 1", 255.0, 0.0, 255.0, 1.0)
    private val alpha2 by setting("Alpha 2", 255.0, 0.0, 255.0, 1.0)
    private val widthSetting by setting("Width", 60.0, 1.0, 400.0, 1.0)
    private val heightSetting by setting("Height", 20.0, 1.0, 400.0, 1.0)
    private val radius by setting("Radius", 5.0, 0.0, 15.0, 0.1)

    override fun getWidth() = widthSetting
    override fun getHeight() = heightSetting

    override fun onRender() {
        val pos1 = pos
        val pos2 = pos.plus(getWidth(), getHeight())

        val c1 = HUD.getColor(0).setAlpha(alpha1.toInt())
        val c2 = HUD.getColor(5).setAlpha(alpha2.toInt())

        if (huy)
            RenderUtils2D.drawGradientRect(pos1, pos2, c1, c1, c2, c2)
        else
            RectBuilder(pos1, pos2).color(c1, c1, c2, c2).radius(radius).draw()

    }
}