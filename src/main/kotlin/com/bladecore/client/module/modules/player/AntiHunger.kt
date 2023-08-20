package com.bladecore.client.module.modules.player

import com.bladecore.client.event.events.PlayerPacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module

object AntiHunger : Module(
    "AntiHunger",
    "Slows down the loss of hunger",
    Category.PLAYER
) {
    init {
        safeListener<PlayerPacketEvent.Misc> {
            it.isSprinting = false
        }
    }
}