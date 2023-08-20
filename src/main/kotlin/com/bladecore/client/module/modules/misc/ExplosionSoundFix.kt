package com.bladecore.client.module.modules.misc

import com.bladecore.client.event.events.PacketEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.mixin.accessor.network.AccessorSPacketSoundEffect
import net.minecraft.init.SoundEvents
import net.minecraft.network.play.server.SPacketSoundEffect
import net.minecraft.util.SoundCategory
import kotlin.math.sin

object ExplosionSoundFix : Module(
    "ExplosionSoundFix",
    "Fixes crystal explosion sound pitch",
    Category.MISC
) {
    private val shift by setting("Shift", -0.2, -0.5, 0.5, 0.05)

    init {
        safeListener<PacketEvent.Receive> {
            val packet = (it.packet as? SPacketSoundEffect) ?: return@safeListener
            if (packet.category != SoundCategory.BLOCKS) return@safeListener
            if (packet.sound != SoundEvents.ENTITY_GENERIC_EXPLODE) return@safeListener

            (packet as AccessorSPacketSoundEffect).setPitch(packet.pitch + shift.toFloat())
        }
    }
}