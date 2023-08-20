package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.AttackEvent
import com.bladecore.client.event.listener.Nameable
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.PacketManager
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.settingName
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.network.play.client.CPacketPlayer

object Criticals : Module(
    "Criticals",
    "Allows to attack with critical hit",
    Category.COMBAT
) {
    private val mode by setting("Mode", Mode.NCP)
    private val onGroundOnly by setting("Only Ground", true)

    private val ncpOldHeight by setting("NCP Old Height", 0.1, 0.001, 0.11, 0.0001, { mode == Mode.NCP_OLD })

    private enum class Mode(override val displayName: String): Nameable {
        NCP("NCP"),
        NCP_OLD("NCP Old"),
        Matrix("Matrix")
    }

    override fun getHudInfo() = mode.settingName

    init {
        safeListener<AttackEvent.Pre> {
            if (!player.onGround && onGroundOnly) return@safeListener
            if (it.entity is EntityEnderCrystal) return@safeListener

            when (mode) {
                Mode.NCP -> {
                    tpY(0.11)
                    tpY(0.1100013579)
                    tpY(0.0000013579)
                }
                Mode.NCP_OLD -> {
                    tpY(ncpOldHeight)
                    tpY(0.0)
                }
                Mode.Matrix -> {
                    tpY(0.2)
                    tpY(0.12160)
                }
            }
        }

        safeListener<AttackEvent.Post> {
            if (!player.onGround && onGroundOnly) return@safeListener
            if (it.entity is EntityEnderCrystal) return@safeListener

            when (mode) {
                Mode.Matrix -> {
                    tpY(0.0, true)
                }
                else -> {}
            }
        }
    }

    private fun SafeClientEvent.tpY(offset: Double, onGround: Boolean = false) {
        val packet = CPacketPlayer.Position(PacketManager.lastReportedPosX, PacketManager.lastReportedPosY + offset, PacketManager.lastReportedPosZ, onGround)
        connection.sendPacket(packet)
    }

}