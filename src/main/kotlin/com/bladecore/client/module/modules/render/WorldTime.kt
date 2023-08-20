package com.bladecore.client.module.modules.render

import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting

object WorldTime : Module(
    "WorldTime",
    "Allows to change world time",
    Category.RENDER
) {
    val time by setting("Time", 0.0, 0.0, 24000.0, 600.0)
}