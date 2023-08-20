package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.MoveEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.PacketManager
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.entity.calcIsInWater
import com.bladecore.client.utils.math.MathUtils.clamp
import net.minecraft.util.math.Vec3d
import kotlin.math.floor

object HitboxDesync : Module(
    "HitboxDesync",
    "Fucks CA",
    Category.COMBAT
) {
    private val moveSpeed by setting("Move Speed", 0.2873, 0.01, 0.3, 0.01)
    private val autoDisable by setting("Auto Disable", true)

    private const val MAGIC_OFFSET = 0.20000996883536

    init {
        safeListener<MoveEvent>(-10) {
            if (!shouldWork()) return@safeListener

            val dir = Vec3d(player.horizontalFacing.directionVec)

            val x = if (dir.x == 0.0) player.posX else (floor(player.posX) + 0.5) + (dir.x * MAGIC_OFFSET)
            val z = if (dir.z == 0.0) player.posZ else (floor(player.posZ) + 0.5) + (dir.z * MAGIC_OFFSET)

            val motionX = clamp(x - player.posX, -moveSpeed, moveSpeed)
            val motionZ = clamp(z - player.posZ, -moveSpeed, moveSpeed)

            player.setPosition(player.posX + motionX, player.posY, player.posZ + motionZ)
            PacketManager.lastReportedPosY = -1.0

            if (player.positionVector.distanceTo(Vec3d(x, player.posY, z)) < 0.015 && autoDisable)
                setEnabled(false)
        }
    }

    private fun SafeClientEvent.shouldWork(): Boolean {
        return !player.capabilities.isFlying &&
            !player.isElytraFlying &&
            !player.calcIsInWater() &&
            !player.isInLava &&
            player.onGround
    }
}