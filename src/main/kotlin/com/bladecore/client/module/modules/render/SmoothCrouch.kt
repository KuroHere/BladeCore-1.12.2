package com.bladecore.client.module.modules.render

import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.MathUtils.lerp
import com.bladecore.client.utils.math.MathUtils.toInt
import com.bladecore.client.utils.render.GLUtils

object SmoothCrouch : Module(
    "SmoothCrouch",
    "Throws exceptions",
    Category.RENDER
) {
    private val speed by setting("Speed", 1.0, 0.1, 5.0, 0.1)
    private val height by setting("Height", 1.0, 0.5, 3.0, 0.1)

    private var crouchProgress = 0.0

    init {
        safeListener<Render3DEvent> {
            crouchProgress = lerp(crouchProgress, player.isSneaking.toInt().toDouble(), GLUtils.deltaTimeDouble() * 10.0 * speed)
        }

        listener<ConnectionEvent.Connect> {
            crouchProgress = 0.0
        }

        listener<ConnectionEvent.Disconnect> {
            crouchProgress = 0.0
        }
    }

    @JvmStatic
    fun getCrouchProgress(): Float {
        return (crouchProgress * height * 0.1).toFloat()
    }

    override fun onEnable() {
        crouchProgress = 0.0
    }
}