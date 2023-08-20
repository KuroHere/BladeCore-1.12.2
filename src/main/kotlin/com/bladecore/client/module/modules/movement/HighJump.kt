package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.events.JumpMotionEvent
import com.bladecore.client.event.events.PlayerPacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.mixins.jumpTicks
import com.bladecore.client.utils.extension.settingName

object HighJump: Module(
    "HighJump",
    "Controls jump motion",
    Category.MOVEMENT
){
    private val mode by setting("Mode", Mode.Vanilla)

    // vanilla
    private val motion by setting("Motion", 0.42, 0.01, 1.0, 0.0001, { mode == Mode.Vanilla })

    private val jumpDelay by setting("Jump Delay", 2.0, 1.0, 5.0, 1.0, { mode == Mode.Matrix })
    private val maxHeight by setting("Max Height", 1.0, 0.1, 3.0, 0.1, { mode == Mode.Matrix })

    private enum class Mode {
        Vanilla,
        Matrix
    }

    override fun getHudInfo() = mode.settingName


    init {
        safeListener<PlayerPacketEvent.Data> {
            if (mode != Mode.Matrix) return@safeListener

            val bb = player.entityBoundingBox
                .offset(0.0, -maxHeight, 0.0)
                .grow(0.3, 0.0, 0.3)

            if (world.getCollisionBoxes(player, bb).isNotEmpty() &&
                player.ticksExisted % jumpDelay.toInt() == 0 &&
                mc.gameSettings.keyBindJump.isKeyDown)
            {
                player.jumpTicks = 0
                player.fallDistance = 0f
                it.onGround = true
                player.onGround = true
            }
        }

        safeListener<JumpMotionEvent> {
            if (mode != Mode.Vanilla) return@safeListener

            it.motion = motion.toFloat()
        }
    }
}