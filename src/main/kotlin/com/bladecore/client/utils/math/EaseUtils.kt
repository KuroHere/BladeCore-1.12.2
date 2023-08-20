package com.bladecore.client.utils.math

import com.bladecore.client.utils.math.MathUtils.clamp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object EaseUtils {
    enum class EaseType{
        InSine, OutSine, InOutSine, InQuad, OutQuad, InOutQuad, InCubic, OutCubic, InOutCubic
    }

    fun getEase(value: Double, type: EaseType, clamp: Boolean = true): Double {
        val x = if (clamp) clamp(value, 0.0, 1.0) else value
        return when(type){
            EaseType.InSine -> 1 - cos((x * PI) / 2)
            EaseType.OutSine -> sin((x * PI) / 2)
            EaseType.InOutSine -> -(cos(PI * x) - 1) / 2
            EaseType.InQuad -> x * x
            EaseType.OutQuad -> 1 - (1 - x) * (1 - x)
            EaseType.InOutQuad -> if (x < 0.5) { 2 * x * x } else { 1 - (-2 * x + 2).pow(2) / 2 }
            EaseType.InCubic -> x * x * x
            EaseType.OutCubic -> 1 - (1 - x).pow(3)
            EaseType.InOutCubic -> if (x < 0.5) { 4 * x * x * x } else { 1 - (-2 * x + 2).pow(3) / 2 }
        }
    }

    fun Double.ease(type: NewEaseType) =
        type.getValue(this)
}

@Suppress("UNUSED")
enum class NewEaseType(val getValue: (valueIn: Double) -> Double) {
    OutBack({
        val c1 = 1.70158
        val c3 = 2.70158
        (1.0 + c3 * (it - 1.0).pow(3.0) + c1 * (it - 1.0).pow(2.0))
    })
}