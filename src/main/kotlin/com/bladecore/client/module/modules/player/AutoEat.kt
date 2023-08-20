package com.bladecore.client.module.modules.player

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.mixins.pressed
import net.minecraft.item.ItemAppleGold
import net.minecraft.item.ItemFood
import net.minecraftforge.fml.common.gameevent.TickEvent

object AutoEat : Module(
    "AutoEat",
    "Automatically ate food",
    Category.PLAYER
) {
    private val health by setting("Health Threshold", 16.0, 1.0, 20.0, 1.0)
    //private val hunger by setting("Hunger Threshold", 0.0, 1.0, 20.0, 1.0)
    private val offhandOnly by setting("Offhand Only", false)
    private val goldenAppleOnly by setting("Golden Apple Only", false)
    val antiReset by setting("Anti Reset", true)
    var isActive = false; private set

    override fun onEnable() {
        isActive = false
    }

    override fun onDisable() {
        mc.gameSettings.keyBindUseItem.pressed = false
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if(mc.player.health <= health && checkItem()){
                isActive = true
                mc.gameSettings.keyBindUseItem.pressed = true
            } else if(isActive){
                mc.gameSettings.keyBindUseItem.pressed = false
                isActive = false
            }
        }
    }

    private fun checkItem():Boolean {
        if (goldenAppleOnly){
            if (mc.player.heldItemOffhand.item is ItemAppleGold) return true
            if (mc.player.heldItemMainhand.item is ItemAppleGold && !offhandOnly) return true
        } else {
            if (mc.player.heldItemOffhand.item is ItemFood) return true
            if (mc.player.heldItemMainhand.item is ItemFood && !offhandOnly) return true
        }

        return false
    }
}