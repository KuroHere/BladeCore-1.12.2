package com.bladecore.client.module.modules.render

import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import java.awt.Color

object GlintColor : Module(
    "GlintColor",
    "Colored nword",
    Category.RENDER
) {
    @JvmStatic
    val color by setting("Color", Color(128, 64, 204))
}