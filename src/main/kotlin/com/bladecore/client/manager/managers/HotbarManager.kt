package com.bladecore.client.manager.managers

import com.bladecore.client.event.EventBus
import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.events.PlayerHotbarSlotEvent
import com.bladecore.client.event.listener.runSafe
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.Manager
import net.minecraft.network.play.client.CPacketHeldItemChange

object HotbarManager : Manager("HotbarManager") {
    var lastReportedSlot = 1; private set
    var lastSwapTime = 0L; private set
    fun handleSlotUpdate() = runSafe { updateSlot() }

    fun SafeClientEvent.updateSlot() {
        val event = PlayerHotbarSlotEvent(player.inventory.currentItem)
        EventBus.post(event)
        val slot = event.slot

        sendSlotPacket(slot)
    }

    fun SafeClientEvent.sendSlotPacket(slot: Int) {
        if (slot != lastReportedSlot) {
            lastReportedSlot = slot
            lastSwapTime = System.currentTimeMillis()
            val packet = CPacketHeldItemChange(lastReportedSlot)
            connection.sendPacket(packet)
        }
    }

    init {
        safeListener<ConnectionEvent.Connect> { lastReportedSlot = 1 }
        safeListener<ConnectionEvent.Disconnect> { lastReportedSlot = 1 }
    }
}