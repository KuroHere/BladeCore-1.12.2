package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.events.MoveEvent
import com.bladecore.client.event.events.TimerEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.settingName

object FastFall: Module(
    "FastFall",
    "Allows you to control fall speed",
    Category.MOVEMENT
) {
    private val mode by setting("Mode", Mode.Motion)
    private val minDist by setting("Min Fall Distance", 3.0, 0.1, 10.0, 0.1)

    private val motionSpeed by setting("Motion Speed", 1.0, 0.01, 10.0, 0.01, visible = {mode == Mode.Motion})
    private val timerSpeed by setting("Timer Speed", 2.0, 0.1, 10.0, 0.1, visible = {mode == Mode.Timer})

    override fun getHudInfo() = mode.settingName

    private enum class Mode {
        Motion,
        Timer
    }

    init {
        safeListener<TimerEvent>(-51) {
            if (player.fallDistance < minDist || player.onGround || player.isInWater || player.isInLava) return@safeListener
            if (mode != Mode.Timer) return@safeListener

            it.speed = timerSpeed
        }

        safeListener<MoveEvent> {
            if (player.fallDistance < minDist || player.onGround || player.isInWater || player.isInLava) return@safeListener
            if (mode != Mode.Motion) return@safeListener

            player.motionY -= motionSpeed * 0.1
        }
    }

}