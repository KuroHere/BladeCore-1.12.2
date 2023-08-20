package com.bladecore.client.module.modules.player

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.utils.extension.mixins.rightClickDelayTimer
import net.minecraftforge.fml.common.gameevent.TickEvent

object FastUse: Module(
    "FastUse",
    "Increases item use speed",
    Category.PLAYER
) {
    init {
        safeListener<TickEvent.ClientTickEvent> {
            mc.rightClickDelayTimer = 0
        }
    }

    override fun onDisable() {
        mc.rightClickDelayTimer = 6
    }
}