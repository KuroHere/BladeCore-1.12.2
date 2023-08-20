package com.bladecore.client.module.modules.render

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import net.minecraftforge.fml.common.gameevent.TickEvent

object CustomFov : Module(
    "CustomFov",
    "Allows to break the fov limit",
    Category.RENDER
){
    private val fov by setting("FOV", 90.0, 40.0, 140.0, 1.0)
    val static by setting("Static", true)
    val allowSprint by setting("Allow Sprint", true, visible = { static })

    init {
        safeListener<TickEvent.ClientTickEvent> {
            mc.gameSettings.fovSetting = fov.toFloat()
        }
    }
}