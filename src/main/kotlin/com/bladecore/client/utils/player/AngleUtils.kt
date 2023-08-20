package com.bladecore.client.utils.player

import net.minecraft.util.EnumFacing

object AngleUtils {
    val EnumFacing.yaw: Float
        get() = when (this) {
            EnumFacing.NORTH -> -180.0f
            EnumFacing.SOUTH -> 0.0f
            EnumFacing.EAST -> -90.0f
            EnumFacing.WEST -> 90.0f
            else -> 0.0f
        }
}