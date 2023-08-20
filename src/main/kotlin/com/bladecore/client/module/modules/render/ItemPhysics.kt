package com.bladecore.client.module.modules.render

import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting

object ItemPhysics : Module(
    "ItemPhysics",
    "Makes item renderer better",
    Category.RENDER
){
    val size by setting("Size", 1.0, 0.3, 1.5, 0.1)
}