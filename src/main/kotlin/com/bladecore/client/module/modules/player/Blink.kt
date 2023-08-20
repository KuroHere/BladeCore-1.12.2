package com.bladecore.client.module.modules.player

import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.events.PacketEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.event.listener.runSafe
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import net.minecraft.network.play.client.CPacketPlayer

object Blink : Module(
    "Blink",
    "Holds player packets",
    Category.PLAYER
) {
    private val packets = ArrayList<CPacketPlayer>()

    init {
        safeListener<PacketEvent.Send> {
            if (it.packet !is CPacketPlayer) return@safeListener
            it.cancel()
            packets.add(it.packet)
        }

        listener<ConnectionEvent.Connect> { packets.clear() }
        listener<ConnectionEvent.Disconnect> { packets.clear() }
    }

    override fun onEnable() { packets.clear() }

    override fun onDisable() {
        runSafe { packets.forEach { connection.sendPacket(it) } }
        packets.clear()
    }
}