package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.utils.extension.mixins.jumpTicks
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object NoJumpDelay: Module("NoJumpDelay", "Removes delay between jumps", Category.MOVEMENT) { init { safeListener<ClientTickEvent> { player.jumpTicks = 0 } } } // epic