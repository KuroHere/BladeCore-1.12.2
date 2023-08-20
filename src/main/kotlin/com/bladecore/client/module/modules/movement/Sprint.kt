package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.events.MoveEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.utils.player.MovementUtils.isInputting
import org.lwjgl.input.Keyboard

object Sprint : Module(
    "Sprint",
    "Sprinting if possible",
    Category.MOVEMENT
) {

    init {
        safeListener<MoveEvent> {
            player.isSprinting = isInputting() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode) && !player.collidedHorizontally
        }
    }
}