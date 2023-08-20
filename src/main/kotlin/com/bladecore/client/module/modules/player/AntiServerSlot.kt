package com.bladecore.client.module.modules.player

import com.bladecore.client.event.events.PacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.HotbarManager
import com.bladecore.client.module.Module
import com.bladecore.client.utils.player.PacketUtils.send
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.server.SPacketHeldItemChange

object AntiServerSlot : Module(
    "AntiServerSlot",
    "Prevents server from changing hotbar slot (may cause desync)",
    Category.PLAYER
) {
    init {
        safeListener<PacketEvent.Receive> { event ->
            if (event.packet !is SPacketHeldItemChange) return@safeListener
            event.cancel()
            CPacketHeldItemChange(HotbarManager.lastReportedSlot).send()
        }
    }
}