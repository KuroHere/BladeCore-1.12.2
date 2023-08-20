package com.bladecore.client.utils.math

import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.*

object MathUtils {
    fun Double.roundToPlaces(places: Int): Double {
        val scale = 10.0.pow(places.toDouble())
        return round(this * scale) / scale
    }

    fun decimalPlaces(value: Double) = value.toString().split('.').getOrElse(1) { "0" }.length

    fun Boolean.toInt(): Int {
        return if (this) 1 else 0
    }

    fun Boolean.toIntSign(): Int {
        return if (this) 1 else -1
    }

    fun Double.floorToInt() = floor(this).toInt()
    fun Double.ceilToInt() = ceil(this).toInt()

    fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) + start

    fun clamp(value: Double, min: Double, max: Double): Double {
        return max(min(value, max), min)
    }

    fun clamp(value: Float, min: Float, max: Float): Float {
        return max(min(value, max), min)
    }

    fun lerp(a: Float, b: Float, t: Float): Float {
        return a + ((b - a) * clamp(t, 0.0f, 1.0f))
    }

    fun lerp(a: Double, b: Double, t: Double): Double {
        return a + ((b - a) * clamp(t, 0.0, 1.0))
    }

    fun lerp(a: Vec3d, b: Vec3d, t: Double): Vec3d {
        val x = lerp(a.x, b.x, t)
        val y = lerp(a.y, b.y, t)
        val z = lerp(a.z, b.z, t)

        return Vec3d(x, y, z)
    }

    fun lerp(a: Vec2d, b: Vec2d, t: Double): Vec2d {
        val x = lerp(a.x, b.x, t)
        val y = lerp(a.y, b.y, t)

        return Vec2d(x, y)
    }

    fun normalize(value: Double, minIn: Double, maxIn: Double, minOut: Double, maxOut: Double) =
        lerp(minOut, maxOut, (value - minIn) / (maxIn - minIn))
}