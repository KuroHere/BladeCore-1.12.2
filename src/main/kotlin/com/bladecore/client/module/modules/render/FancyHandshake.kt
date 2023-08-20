package com.bladecore.client.module.modules.render

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import net.minecraftforge.client.event.RenderSpecificHandEvent
import org.lwjgl.opengl.GL11

object FancyHandshake: Module(
    "FancyHandshake",
    "Allows to control arm bobbing amount",
    Category.RENDER
) {
    private val amount by setting("Amount", 1.0, 0.5, 5.0, 0.5)
    init {
        safeListener<RenderSpecificHandEvent> {
            GL11.glScaled(amount, amount, amount) // -_- questions?
        }
    }
}