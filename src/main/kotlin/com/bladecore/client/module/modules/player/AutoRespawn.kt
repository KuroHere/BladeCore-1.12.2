package com.bladecore.client.module.modules.player

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraftforge.fml.common.gameevent.TickEvent

object AutoRespawn : Module(
    "AutoRespawn",
    "Automatically respawns you",
    Category.PLAYER
) {
    init {
        safeListener<TickEvent.ClientTickEvent>{
            if (!player.isDead) return@safeListener
            connection.sendPacket(CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN))
        }
    }
}