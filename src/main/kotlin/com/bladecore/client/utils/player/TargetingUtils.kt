package com.bladecore.client.utils.player

import com.bladecore.client.manager.managers.FriendManager.isFriend
import com.bladecore.client.module.modules.combat.AntiBot.isBot
import com.bladecore.client.module.modules.combat.KillAura
import com.bladecore.client.utils.player.RotationUtils.getEyePosition
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityIronGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntityShulker
import net.minecraft.entity.passive.EntityAmbientCreature
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.player.EntityPlayer

object TargetingUtils {
    val mc: Minecraft = Minecraft.getMinecraft()

    fun getTargetList(
        players: Boolean = KillAura.players,
        friends: Boolean = KillAura.friends,
        hostile: Boolean = KillAura.hostileMobs,
        animal: Boolean = KillAura.animals,
        invisible: Boolean = KillAura.invisible,
    ): ArrayList<EntityLivingBase> {
        return ArrayList(mc.player.world.loadedEntityList.filterIsInstance<EntityLivingBase>().filter { e ->
            if ((e.isBot()) ||
                (e == mc.renderViewEntity) ||
                (e == mc.player) ||
                (e.isInvisible && !invisible) ||
                e.isDead ||
                (e.health <= 0)
            ) return@filter false

            if ((e is EntityPlayer) && (players && !e.isSpectator && (!e.isFriend() || friends))) return@filter true
            if (hostile && e.isHostile) return@filter true
            if (animal && e.isPassive) return@filter true

            return@filter false
        })
    }

    val EntityLivingBase.isPassive
        get() = this is EntityAnimal
            || this is EntityAgeable
            || this is EntityAmbientCreature
            || this is EntitySquid

    val EntityLivingBase.isHostile
        get() = this is EntityMob
            || this is EntityShulker
            || this is EntityIronGolem
            || this is EntityDragon
            || this is EntityGhast

    fun getTarget(reach: Double, ignoreWalls: Boolean): EntityLivingBase?{
        if (mc.player.isDead) return null

        return getTargetList()
            .filter { (it.getDistance(mc.player) < reach) && (mc.player.canEntityBeSeen(it) || ignoreWalls) }
            .minByOrNull { it.getDistance(getEyePosition().x, getEyePosition().y, getEyePosition().z) }
    }

}