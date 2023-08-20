package com.bladecore.client.module.modules.player

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.gameevent.TickEvent

object Reach : Module(
    "Reach",
    "Increases reach distance",
    Category.PLAYER
) {

    private val amount by setting("Amount", 0.5, 0.1, 5.0, 0.1)

    init {
        safeListener<TickEvent.ClientTickEvent> {
            mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(mc.player.uniqueID)
            mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(AttributeModifier(mc.player.uniqueID, "custom_reach", amount, 1))
        }
    }


    override fun onDisable() {
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(mc.player.uniqueID)
    }
}