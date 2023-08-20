package com.bladecore.client.module.modules.player

import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import net.minecraft.item.ItemPickaxe
import net.minecraft.util.math.RayTraceResult

object NoEntityTrace : Module(
    "NoEntityTrace",
    "NoEntityTrace",
    Category.PLAYER
) {
    private val checkPickaxe by setting("Check Pickaxe", true)


    @JvmStatic
    fun isActive(): Boolean {
        if (!isEnabled()) return false

        val holdingPickAxe = mc.player?.heldItemMainhand?.item is ItemPickaxe

        return !checkPickaxe || holdingPickAxe
    }
}