package com.bladecore.client.utils.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object VecUtils {
    fun BlockPos.toVec3d(): Vec3d {
        return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun BlockPos.toVec3dCenter(): Vec3d {
        return this.toVec3d().add(0.5, 0.5, 0.5)
    }

    infix fun Vec3d.dist(vec: Vec3d) =
        this.distanceTo(vec)
}