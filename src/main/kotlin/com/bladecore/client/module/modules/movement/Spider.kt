package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.events.MoveEvent
import com.bladecore.client.event.events.PlayerPacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.settingName
import com.bladecore.client.utils.math.MSTimer
import com.bladecore.client.utils.player.MovementUtils.isInputting

object Spider: Module(
    "Spider",
    "Allows you to climb the wall like a spider",
    Category.MOVEMENT
) {
    private val mode by setting("Mode", Mode.Vanilla)
    private val climbSpeed by setting("Climb Delay", 1.0, 0.1, 5.0, 0.1, { mode == Mode.Matrix })

    private enum class Mode {
        Vanilla,
        Matrix
    }

    override fun getHudInfo() = mode.settingName

    private val timer = MSTimer()

    init {
        safeListener<MoveEvent>{
            if(!mc.player.collidedHorizontally || mc.player.isInWater || mc.player.isInLava || !isInputting()) return@safeListener

            when(mode) {
                Mode.Vanilla -> player.motionY = 0.2
                else -> {}
            }
        }

        safeListener<PlayerPacketEvent.Data> {
            if(!mc.player.collidedHorizontally || mc.player.isInWater || mc.player.isInLava || !isInputting()) return@safeListener
            if (mode != Mode.Matrix || !timer.hasReached(100.0 * climbSpeed)) return@safeListener

            it.onGround = true
            player.jump()
            timer.reset()
        }
    }
}