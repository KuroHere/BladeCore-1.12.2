package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.events.PacketEvent
import com.bladecore.client.event.events.PushByEntityEvent
import com.bladecore.client.event.events.PushOutOfBlocksEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.mixins.entityVelocityMotionX
import com.bladecore.client.utils.extension.mixins.entityVelocityMotionY
import com.bladecore.client.utils.extension.mixins.entityVelocityMotionZ
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion

object Velocity : Module(
    "Velocity",
    "Controls your velocity",
    Category.COMBAT
) {
    private val noKnockBack by setting("No Knock Back", true)
    private val noEntityPush by setting("No Entity Push", true)
    private val noBlockPush by setting("No Block Push", true)
    private val explosion by setting("Explosion", true)

    init {
        safeListener<PacketEvent.Receive> {
            if (it.packet is SPacketEntityVelocity) {
                if (it.packet.entityID != player.entityId) return@safeListener
                if (noKnockBack) it.cancel()
            }

            if (it.packet is SPacketExplosion && explosion) it.cancel()
        }

        safeListener<PushOutOfBlocksEvent> {
            if(noBlockPush) it.cancel()
        }

        safeListener<PushByEntityEvent> {
            if(noEntityPush) it.cancel()
        }
    }
}