package com.bladecore.client.module.modules.player

import com.bladecore.client.event.events.PacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.utils.extension.mixins.playerPosLookPitch
import com.bladecore.client.utils.extension.mixins.playerPosLookYaw
import net.minecraft.network.play.server.SPacketPlayerPosLook

object AntiRotate : Module(
    "AntiRotate",
    "Cancel server's rotation packets",
    Category.PLAYER
) {
    init {
        safeListener<PacketEvent.Receive> {
            if (it.packet !is SPacketPlayerPosLook) return@safeListener
            it.packet.playerPosLookYaw = player.rotationYaw
            it.packet.playerPosLookPitch = player.rotationPitch
        }
    }
}