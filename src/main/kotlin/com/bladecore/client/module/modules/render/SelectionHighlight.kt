package com.bladecore.client.module.modules.render

import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.utils.render.esp.ESPRenderInfo
import com.bladecore.client.utils.render.esp.ESPRenderer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import java.awt.Color

object SelectionHighlight : Module(
    "SelectionHighlight",
    "BOX!",
    Category.RENDER
) {
    private val filledColor by setting("Filled Color", Color(40, 200, 250, 60))
    private val outlineColor by setting("Outline Color", Color(40, 200, 250, 150))
    private val outlineWidth by setting("Outline Width", 1.0, 1.0, 5.0, 0.25)
    private val allSides by setting("All Sides", true)
    private val fullOutline by setting("Full Outline", false, { !allSides })

    private var info: ESPRenderInfo? = null

    init {
        safeListener<Render3DEvent> {
            info?.draw()
        }
    }

    @JvmStatic
    fun draw(result: RayTraceResult) {
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) {
            info = null
            return
        }

        val pos = result.blockPos
        val sides = if (allSides) EnumFacing.values().toList() else listOf(result.sideHit)

        info = ESPRenderInfo(
            pos,
            filledColor,
            outlineColor,
            sides,
            fullOutline,
            outlineWidth.toFloat()
        )
    }

}