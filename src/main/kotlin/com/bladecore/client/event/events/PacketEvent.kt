package com.bladecore.client.event.events

import com.bladecore.client.event.Cancellable
import com.bladecore.client.event.Event
import com.bladecore.client.event.ICancellable
import net.minecraft.network.Packet

abstract class PacketEvent(val packet: Packet<*>) : Event, ICancellable by Cancellable() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class PostReceive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
    class PostSend(packet: Packet<*>) : PacketEvent(packet)
}