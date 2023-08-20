package com.bladecore.client.utils.world

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.utils.math.MathUtils.ceilToInt
import com.bladecore.client.utils.math.MathUtils.floorToInt
import net.minecraft.block.material.Material
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.util.CombatRules
import net.minecraft.util.DamageSource
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Explosion
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

object CrystalUtils {
    fun getBlockPosInSphere(center: Vec3d, radius: Float): ArrayList<BlockPos> {
        val squaredRadius = radius.pow(2)
        val posList = ArrayList<BlockPos>()

        fun getAxisRange(d1: Double, d2: Float): IntRange {
            return IntRange((d1 - d2).floorToInt(), (d1 + d2).ceilToInt())
        }

        for (x in getAxisRange(center.x, radius)) for (y in getAxisRange(center.y, radius)) for (z in getAxisRange(center.z, radius)) {
            /* Valid position check */
            val blockPos = BlockPos(x, y, z)
            if (blockPos.distanceSqToCenter(center.x, center.y, center.z) > squaredRadius) continue
            posList.add(blockPos)
        }
        return posList
    }

    fun SafeClientEvent.getCrystalList(center: Vec3d, range: Float): List<EntityEnderCrystal> =
        world.loadedEntityList.toList()
            .filterIsInstance<EntityEnderCrystal>()
            .filter { entity -> entity.isEntityAlive && entity.positionVector.distanceTo(center) <= range }


    /* End of position finding */

    /* Damage calculation */

    fun SafeClientEvent.calcCrystalDamage(pos: BlockPos, entity: EntityLivingBase, entityPos: Vec3d? = entity.positionVector, entityBB: AxisAlignedBB? = entity.entityBoundingBox) =
        calcCrystalDamage(Vec3d(pos).add(0.5, 1.0, 0.5), entity, entityPos, entityBB)

    fun SafeClientEvent.calcCrystalDamage(pos: Vec3d, entity: EntityLivingBase, entityPos: Vec3d? = entity.positionVector, entityBB: AxisAlignedBB? = entity.entityBoundingBox): Float {
        // Return 0 directly if entity is a player and in creative mode
        if (entity is EntityPlayer && entity.isCreative) return 0.0f

        // Calculate raw damage (based on blocks and distance)
        var damage = calcRawDamage(pos, entityPos ?: entity.positionVector, entityBB ?: entity.entityBoundingBox)

        // Calculate damage after armor, enchantment, resistance effect absorption
        damage = calcDamage(entity, damage, getDamageSource(pos))

        // Multiply the damage based on difficulty if the entity is player
        if (entity is EntityPlayer) damage *= world.difficulty.id * 0.5f

        return max(damage, 0.0f)
    }

    private fun calcDamage(entity: EntityLivingBase, damageIn: Float = 100f, source: DamageSource = DamageSource.GENERIC, roundDamage: Boolean = false): Float {
        if (entity is EntityPlayer && entity.isCreative) return 0.0f

        val armorValue = entity.totalArmorValue.toFloat()
        val toughness = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).attributeValue.toFloat()

        val armorValues = armorValue to toughness

        var damage = CombatRules.getDamageAfterAbsorb(damageIn, armorValues.first, armorValues.second)

        if (source != DamageSource.OUT_OF_WORLD) {
            entity.getActivePotionEffect(MobEffects.RESISTANCE)?.let {
                damage *= max(1.0f - (it.amplifier + 1) * 0.2f, 0.0f)
            }
        }

        damage *= getProtectionModifier(entity, source)

        return if (roundDamage) round(damage) else damage
    }

    private fun getProtectionModifier(entity: EntityLivingBase, damageSource: DamageSource): Float {
        var modifier = 0

        for (armor in entity.armorInventoryList.toList()) {
            if (armor.isEmpty) continue
            val nbtTagList = armor.enchantmentTagList
            for (i in 0 until nbtTagList.tagCount()) {
                val compoundTag = nbtTagList.getCompoundTagAt(i)

                val id = compoundTag.getInteger("id")
                val level = compoundTag.getInteger("lvl")

                Enchantment.getEnchantmentByID(id)?.let {
                    modifier += it.calcModifierDamage(level, damageSource)
                }
            }
        }

        modifier = modifier.coerceIn(0, 20)

        return 1.0f - modifier / 25.0f
    }

    fun SafeClientEvent.calcRawDamage(pos: Vec3d, entityPos: Vec3d, entityBB: AxisAlignedBB): Float {
        val distance = pos.distanceTo(entityPos)
        val v = (1.0 - (distance / 12.0)) * world.getBlockDensity(pos, entityBB)
        return ((v * v + v) / 2.0 * 84.0 + 1.0).toFloat()
    }

    private fun SafeClientEvent.getDamageSource(damagePos: Vec3d) =
        DamageSource.causeExplosionDamage(Explosion(world, player, damagePos.x, damagePos.y, damagePos.z, 6F, false, true))
    /* End of damage calculation */
}