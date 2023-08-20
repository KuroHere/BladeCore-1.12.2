package com.bladecore.client.utils.player

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.listener.runSafe
import net.minecraft.network.Packet

object PacketUtils {
    fun Packet<*>.send() = runSafe {
        connection.sendPacket(this@send)
    }

    fun Packet<*>.send(event: SafeClientEvent) =
        event.connection.sendPacket(this@send)
}