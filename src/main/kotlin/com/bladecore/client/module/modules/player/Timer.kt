package com.bladecore.client.module.modules.player

import com.bladecore.client.event.events.TimerEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting

object Timer : Module(
    "Timer",
    "Changes client-side TPS",
    Category.PLAYER
) {
    private val multipler by setting("Multipler", 1.0, 0.1, 10.0, 0.01)

    override fun getHudInfo() = multipler.toString()

    init {
        safeListener<TimerEvent>(-49) {
            it.speed = multipler
        }
    }
}