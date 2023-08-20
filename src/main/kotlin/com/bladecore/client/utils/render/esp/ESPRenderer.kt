package com.bladecore.client.utils.render.esp

import com.bladecore.client.manager.managers.ESPManager
import com.bladecore.client.utils.render.RenderTessellator
import com.bladecore.client.utils.render.RenderUtils3D
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.GL_LINES
import org.lwjgl.opengl.GL11.GL_QUADS
import java.awt.Color

class ESPRenderer {
    private var toRender: MutableList<Triple<ESPBox, Pair<Color, Color>, List<EnumFacing>>> = ArrayList()

    var thickness = 1f
    var fullOutline = false

    fun put(box: ESPBox, filledColor: Color, outlineColor: Color, sides: List<EnumFacing> = EnumFacing.values().toList()) =
        put(Triple(box, filledColor to outlineColor, sides))

    fun put(bb: AxisAlignedBB, filledColor: Color, outlineColor: Color, sides: List<EnumFacing> = EnumFacing.values().toList()) =
        put(Triple(bb.toESPBox(), filledColor to outlineColor, sides))

    fun put(pos: BlockPos, filledColor: Color, outlineColor: Color, sides: List<EnumFacing> = EnumFacing.values().toList()) =
        put(Triple(AxisAlignedBB(pos).toESPBox(), filledColor to outlineColor, sides))

    private fun put(triple: Triple<ESPBox, Pair<Color, Color>, List<EnumFacing>>) {
        toRender.add(triple)
    }

    fun clear() =
        toRender.clear()

    fun render(clear: Boolean = true) {
        toRender.forEach {
            val info = ESPRenderInfo(it.first, it.second.first, it.second.second, it.third, fullOutline, thickness)
            ESPManager.put(info)
        }

        if (clear) clear()
    }
}