package com.bladecore.client.module.modules.movement

import com.bladecore.client.event.events.PlayerPacketEvent
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.PacketManager
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraftforge.fml.common.gameevent.TickEvent

object NoFall : Module(
    "NoFall",
    "Reduces fall damage",
    Category.MOVEMENT
) {
    private val mode by setting("Mode", Mode.Spoof)

    private enum class Mode {
        Spoof,
        SpoofDistance,
        SetbackReset
    }

    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (mode != Mode.SetbackReset) return@safeListener
            if (player.fallDistance < 2.5) return@safeListener

            connection.sendPacket(CPacketPlayer.Position(PacketManager.lastReportedPosX, PacketManager.lastReportedPosY + 80.0, PacketManager.lastReportedPosZ, true))
            player.fallDistance = 0f
        }

        safeListener<PlayerPacketEvent.Data> {
            when(mode) {
                Mode.Spoof -> {
                    it.onGround = true
                }

                Mode.SpoofDistance -> {
                    if (player.fallDistance > 2.5) {
                        it.onGround = true
                        player.fallDistance = 0f
                    }
                }

                else -> {}
            }
        }
    }
}