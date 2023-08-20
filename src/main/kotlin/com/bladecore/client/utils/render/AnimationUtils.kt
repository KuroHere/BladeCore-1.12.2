package com.bladecore.client.utils.render

import com.bladecore.client.utils.math.MathUtils.clamp

object AnimationUtils {
    fun getAnimationProgressFloat(startTime: Long, duration: Int): Float {
        return clamp((System.currentTimeMillis() - startTime) / duration.toFloat(), 0.0f, 1.0f)
    }

    fun getAnimationProgressDouble(startTime: Long, duration: Int): Double {
        return clamp((System.currentTimeMillis() - startTime) / duration.toDouble(), 0.0, 1.0)
    }
}