package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.entity.isInsideBlock
import com.bladecore.client.utils.items.HotbarSlot
import com.bladecore.client.utils.items.firstItem
import com.bladecore.client.utils.items.hotbarSlots
import com.bladecore.client.utils.player.ChatUtils
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.common.gameevent.TickEvent

object PearlClip : Module(
    "PearlClip",
    "Allows you to teleport inside blocks",
    Category.MOVEMENT
) {
    private var firstSlot = 0
    private var ticksInCollide = 0

    private val autoDisable by setting("AutoDisable", true)
    init{
        safeListener<TickEvent.ClientTickEvent> {
            if (getPearlSlot() == null && autoDisable) {
                ChatUtils.sendMessage("No EnderPearl in hotbar. Disabling...")
                toggle()
                return@safeListener
            }
            if (mc.player.collidedHorizontally &&
                !player.isInsideBlock && getPearlSlot() != null &&
                mc.gameSettings.keyBindForward.isKeyDown &&
                !mc.gameSettings.keyBindBack.isKeyDown
            ) ticksInCollide++ else ticksInCollide = 0

            if (ticksInCollide == 3) {
                firstSlot = mc.player.inventory.currentItem
                swapTo(getPearlSlot())
            }
            if (ticksInCollide == 5) {
                mc.player.connection.sendPacket(CPacketPlayer.Rotation(mc.player.rotationYaw, 85f, mc.player.onGround))
                throwPearl()
                swapTo(firstSlot)
            }
        }
    }

    private fun throwPearl(){
        mc.playerController.processRightClick(mc.player, mc.player.world, EnumHand.MAIN_HAND)
    }

    private fun getPearlSlot(): HotbarSlot? {
        return mc.player.hotbarSlots.firstItem(Items.ENDER_PEARL)
    }

    private fun swapTo(slot: HotbarSlot?){
        val slotNum = slot?.hotbarSlot
        if (slotNum !in 0..8) return
        if (slot != null){
            mc.player.inventory.currentItem = slot.hotbarSlot
        }
        mc.playerController.updateController()
    }

    private fun swapTo(slotNum: Int){
        if (slotNum !in 0..8) return
        mc.player.inventory.currentItem = slotNum
        mc.playerController.updateController()
    }
}