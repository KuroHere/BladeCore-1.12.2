package com.bladecore.client.module.modules.render

import com.bladecore.client.module.Module

object FullBright : Module(
    "FullBright",
    "Makes everything brighter",
    Category.RENDER
) {
    override fun onEnable() {
        mc.gameSettings.gammaSetting = 1000f
        setEnabled(false)
    }
}