package com.bladecore.client.manager.managers

import com.bladecore.client.event.EventBus
import com.bladecore.client.event.events.TimerEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.event.listener.runSafe
import com.bladecore.client.manager.Manager
import com.bladecore.client.utils.extension.mixins.tickLength
import com.bladecore.client.utils.extension.mixins.timer
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.gameevent.TickEvent

object TimerManager : Manager("TimerManager") {
    init {
        listener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.END) return@listener
            val timer = Minecraft.getMinecraft().timer

            TimerEvent().apply {
                runSafe {
                    EventBus.post(this@apply)
                }

                timer.tickLength = 50.0f / speed.toFloat()
            }
        }
    }

    val timerSpeed get() = 50.0 / Minecraft.getMinecraft().timer.tickLength
}