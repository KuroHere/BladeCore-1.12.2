package com.bladecore.client.module.modules.player

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.PlayerHotbarSlotEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.HotbarManager.sendSlotPacket
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.items.HotbarSlot
import com.bladecore.client.utils.items.hotbarSlots
import com.bladecore.client.utils.render.ColorUtils.setAlpha
import com.bladecore.client.utils.render.esp.AnimatedESPRenderer
import com.bladecore.client.utils.world.WorldUtils.getBlockState
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Blocks
import net.minecraft.init.Enchantments
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color

object PacketMine : Module(
    "PacketMine",
    "Allows to break blocks faster",
    Category.PLAYER
) {
    private val maxMineTicks by setting("Max Mine Ticks", 20.0, 10.0, 100.0, 1.0)
    private val spoofHotbar by setting("Spoof Hotbar", false)
    private val spoofBypass by setting("Spoof Bypass", false, { spoofHotbar })
    private val swapDelay by setting("Swap Delay", 10.0, 1.0, 50.0, 1.0, { spoofHotbar })

    private var block: BlockPos? = null
    private val renderer = AnimatedESPRenderer { Triple(Color.GREEN.setAlpha(50), Color.GREEN.setAlpha(120), 1.0f) }

    private var mineTicks = 0

    init {
        safeListener<Render3DEvent> {
            renderer.setPosition(block)
            renderer.draw()
        }

        safeListener<TickEvent.ClientTickEvent> {
            update()
        }

        safeListener<PlayerHotbarSlotEvent> { event ->
            block?.let {
                getBestSlot(it.getBlockState())?.let { slot ->
                    if (mineTicks < swapDelay || !spoofHotbar) return@safeListener
                    event.slot = slot.hotbarSlot
                }
            }
        }
    }

    private fun SafeClientEvent.getBestSlot(blockState: IBlockState): HotbarSlot? {
        return player.hotbarSlots.maxByOrNull {
            val stack = it.stack
            if (stack.isEmpty) {
                0.0f
            } else {
                var speed = stack.getDestroySpeed(blockState)

                if (speed > 1.0f) {
                    val efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)
                    if (efficiency > 0) {
                        speed += efficiency * efficiency + 1.0f
                    }
                }

                speed
            }
        }
    }

    private fun SafeClientEvent.update() {
        val viewEntity = mc.renderViewEntity ?: player
        val eyePos = viewEntity.getPositionEyes(mc.renderPartialTicks)
        if (!world.isAirBlock(BlockPos(eyePos.x, eyePos.y, eyePos.z))) return
        val hitObject = mc.objectMouseOver ?: return

        if (hitObject.typeOfHit == RayTraceResult.Type.BLOCK &&
            mc.gameSettings.keyBindAttack.isKeyDown &&
            hitObject.blockPos != block
        ) breakBlock(hitObject.blockPos, hitObject.sideHit)

        block?.let { if (it.getBlockState().block == Blocks.AIR) block = null }

        if (block == null) mineTicks = 0 else mineTicks++
        if (mineTicks > maxMineTicks) block = null
    }

    private fun SafeClientEvent.breakBlock(pos: BlockPos, side: EnumFacing) {
        val slot = getBestSlot(pos.getBlockState())?.hotbarSlot
        if (spoofBypass && slot != null)
            sendSlotPacket(slot)

        val packetStart = CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side)
        val packetStop = CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side)

        connection.sendPacket(packetStart)
        connection.sendPacket(packetStop)

        if (spoofBypass)
            sendSlotPacket(player.inventory.currentItem)

        block = pos
    }

    override fun onEnable() {
        block = null
        mineTicks = 0
        renderer.reset()
    }
}