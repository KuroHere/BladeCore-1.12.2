package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.items.SlotInfo
import com.bladecore.client.utils.items.SlotInfo.Companion.offhand
import com.bladecore.client.utils.items.firstStack
import com.bladecore.client.utils.player.MovementUtils.isInputting
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Items
import net.minecraft.inventory.ClickType
import net.minecraftforge.fml.common.gameevent.TickEvent

object OffhandManager : Module(
    "OffhandManager",
    "Automatically place items to your offhand slot",
    Category.COMBAT
) {
    private val page by setting("Page", Page.General)

    private val standingOnly by setting("Standing Only", false, { page == Page.General })

    private val useTotem by setting("Use Totem", true, { page == Page.Totem })
    private val totemHealth by setting("Totem Health", 5.0, 1.0, 20.0, 0.1, { page == Page.Totem && useTotem })

    private val useCrystal by setting("Use Crystal", false, { page == Page.Crystal })
    private val fillCrystals by setting("Fill Crystals", false, { page == Page.Crystal && useCrystal })
    private val fillThreshold by setting("Fill Threshold", 20.0, 4.0, 64.0, 1.0, { page == Page.Crystal && useCrystal && fillCrystals })

    private enum class Page {
        General,
        Totem,
        Crystal
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (mc.currentScreen is GuiContainer) return@safeListener
            if (standingOnly && isInputting()) return@safeListener

            fillCrystals()

            val item = getItem() ?: return@safeListener
            if (offhand.stack.item == item.stack.item) return@safeListener

            replace(item)
        }
    }

    private fun SafeClientEvent.getItem(): SlotInfo? {
        val totem = player.inventory.firstStack(Items.TOTEM_OF_UNDYING)
        val crystal = player.inventory.firstStack(Items.END_CRYSTAL)

        if (totem != null && useTotem && player.health <= totemHealth)
            return totem

        if (crystal != null && useCrystal)
            return crystal

        return null
    }

    private fun replace(info: SlotInfo) {
        if (!offhand.stack.isEmpty)
            offhand.click(0, ClickType.PICKUP)

        info.click(0, ClickType.PICKUP)
        offhand.click(0, ClickType.PICKUP)
    }

    private fun SafeClientEvent.fillCrystals() {
        if (!useCrystal || !fillCrystals) return
        if (offhand.stack.item != Items.END_CRYSTAL) return
        if (offhand.stack.count >= fillThreshold) return

        val crystal = player.inventory.firstStack(Items.END_CRYSTAL) ?: return
        crystal.click(0, ClickType.PICKUP)
        offhand.click(0, ClickType.PICKUP)
        crystal.click(0, ClickType.PICKUP)
    }
}