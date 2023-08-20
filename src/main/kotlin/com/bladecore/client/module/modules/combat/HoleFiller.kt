package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.PlayerHotbarSlotEvent
import com.bladecore.client.event.events.PlayerPacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.HotbarManager
import com.bladecore.client.manager.managers.HotbarManager.updateSlot
import com.bladecore.client.manager.managers.PacketManager
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.items.HotbarSlot
import com.bladecore.client.utils.items.firstBlock
import com.bladecore.client.utils.items.hotbarSlots
import com.bladecore.client.utils.player.RotationUtils
import com.bladecore.client.utils.world.HoleType
import com.bladecore.client.utils.world.HoleUtils.checkHole
import com.bladecore.client.utils.world.HoleUtils.getHoleType
import com.bladecore.client.utils.world.getBlockSequence
import com.bladecore.client.utils.world.placement.NeighborUtils.getNeighbor
import com.bladecore.client.utils.world.placement.PlaceInfo
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos

object HoleFiller : Module(
    "HoleFiller",
    "Fills holes around player",
    Category.COMBAT
) {
    private val onlyInsideHole by setting("Only Inside Hole", true)
    private val visibilityCheck by setting("Visibility Check", true)
    private val placeDelay by setting("Place Delay", 4.0, 1.0, 20.0, 1.0)
    private val placeReach by setting("Place Reach", 4.25, 3.0, 6.0, 0.05)
    private val sendSneakPacket by setting("Sneak Packet", false)

    private var placeInfo: PlaceInfo? = null
    private var slot: Int? = null
    private var placeTicks = 0

    override fun onDisable() {
        placeInfo = null
        slot = null
        placeTicks = 0
    }

    init {
        safeListener<PlayerHotbarSlotEvent> { event ->
            slot?.let { event.slot = it }
        }

        safeListener<PlayerPacketEvent.Data> { event ->
            placeTicks++

            placeInfo = null
            slot = null

            val holes = getBlockSequence(
                BlockPos(player.positionVector),
                (placeReach + 2.0).toInt(),
                (placeReach).toInt()
            ).filter {
                val type = getHoleType(it)
                type == HoleType.OBSIDIAN || type == HoleType.BEDROCK

            }

            val type = checkHole(player)

            if (holes.isEmpty() ||
                player.hotbarSlots.firstBlock(Blocks.OBSIDIAN) == null ||
                ((type != HoleType.OBSIDIAN && type != HoleType.BEDROCK) && onlyInsideHole)) return@safeListener

            placeInfo = getPlaceInfo(holes) ?: return@safeListener

            RotationUtils.rotationsToVec(placeInfo!!.hitVec).let { event.yaw = it.x; event.pitch = it.y }
            slot = player.hotbarSlots.firstBlock(Blocks.OBSIDIAN)?.hotbarSlot
            updateSlot()
        }

        safeListener<PlayerPacketEvent.Post> {
            val s = player.hotbarSlots.firstBlock(Blocks.OBSIDIAN) ?: return@safeListener
            if (HotbarManager.lastReportedSlot != s.hotbarSlot) return@safeListener

            placeInfo?.let { doPlace(it, s) }
        }
    }


    private fun SafeClientEvent.getPlaceInfo(holeList: List<BlockPos>): PlaceInfo? {
        return holeList
            .mapNotNull { getNeighbor(it, 1, range = placeReach.toFloat(), visibleSideCheck = visibilityCheck) }
            .minByOrNull { player.getPositionEyes(1.0f).distanceTo(it.hitVec) }
    }

    private fun SafeClientEvent.doPlace(placeInfo: PlaceInfo, slot: HotbarSlot) {
        if (placeTicks < placeDelay) return

        if (!world.getBlockState(placeInfo.placedPos).material.isReplaceable) return

        val itemStack = slot.stack
        val block = (itemStack.item as? ItemBlock?)?.block ?: return
        val metaData = itemStack.metadata

        val sneak = !PacketManager.lastReportedSneaking && sendSneakPacket
        if (sneak) connection.sendPacket(CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING))

        connection.sendPacket(placeInfo.getPlacePacket(EnumHand.MAIN_HAND))
        player.swingArm(EnumHand.MAIN_HAND)

        if (sneak) connection.sendPacket(CPacketEntityAction(player, CPacketEntityAction.Action.STOP_SNEAKING))

        val blockState = block.getStateForPlacement(world, placeInfo.pos, placeInfo.side, placeInfo.hitVecOffset.x.toFloat(), placeInfo.hitVecOffset.y.toFloat(), placeInfo.hitVecOffset.z.toFloat(), metaData, player, EnumHand.MAIN_HAND)
        val soundType = blockState.block.getSoundType(blockState, world, placeInfo.pos, player)
        world.playSound(player, placeInfo.pos, soundType.placeSound, SoundCategory.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f)
        world.setBlockState(placeInfo.placedPos, blockState)

        placeTicks = 0
    }
}