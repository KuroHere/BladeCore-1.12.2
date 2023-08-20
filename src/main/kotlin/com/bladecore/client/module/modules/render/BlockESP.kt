package com.bladecore.client.module.modules.render

import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.render.esp.ESPRenderer
import net.minecraft.tileentity.TileEntityChest
import net.minecraft.tileentity.TileEntityEnderChest
import net.minecraft.tileentity.TileEntityShulkerBox
import java.awt.Color

object BlockESP : Module(
    "BlockESP",
    "BlockESP",
    Category.RENDER
) {
    private val chest by setting("Chest", true)
    private val chestFilled by setting("Chest Filled", Color(200, 100, 0, 60), { chest })
    private val chestOutline by setting("Chest Outline", Color(200, 100, 0, 100), { chest })

    private val enderchest by setting("EnderChest", true)
    private val enderChestFilled by setting("EnderChest Filled", Color(180, 0, 200, 60), { enderchest })
    private val enderChestOutline by setting("EnderChest Outline", Color(180, 0, 200, 100), { enderchest })

    private val shulker by setting("Shulker", true)
    private val shulkerFilled by setting("Shulker Filled", Color(180, 0, 200, 60), { shulker })
    private val shulkerOutline by setting("Shulker Outline", Color(180, 0, 200, 100), { shulker })

    private val renderer = ESPRenderer()

    init {
        safeListener<Render3DEvent> {
            world.tickableTileEntities.forEach {
                when (it) {
                    is TileEntityChest -> if (chest) renderer.put(it.pos, chestFilled, chestOutline)
                    is TileEntityEnderChest -> if (enderchest) renderer.put(it.pos, enderChestFilled, enderChestOutline)
                    is TileEntityShulkerBox -> if (shulker) renderer.put(it.pos, shulkerFilled, shulkerOutline)
                }
            }
            renderer.render()
        }
    }
}