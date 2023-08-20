package com.bladecore.client.utils.render

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.utils.extension.mixins.renderPosX
import com.bladecore.client.utils.extension.mixins.renderPosY
import com.bladecore.client.utils.extension.mixins.renderPosZ
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtils3D {
    val mc: Minecraft = Minecraft.getMinecraft()

    val viewerPos get() =
        Vec3d(mc.renderManager.viewerPosX, mc.renderManager.viewerPosY, mc.renderManager.viewerPosZ)

    fun SafeClientEvent.drawTrace(e: EntityLivingBase, partialTicks: Float, color: Color, width: Float) {
        if (mc.renderViewEntity == null) return
        if (mc.renderManager.renderViewEntity == null) return

        val height = e.entityBoundingBox.maxY - e.entityBoundingBox.minY

        GlStateManager.enableBlend()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glLineWidth(width)
        GL11.glPushMatrix()
        GL11.glDepthMask(false)
        GL11.glColor4d(color.red / 255.0, color.green / 255.0, color.blue / 255.0, color.alpha / 255.0)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBegin(GL11.GL_LINES)
        val v = Vec3d(0.0, 0.0, 1.0).rotatePitch(-Math.toRadians(mc.renderViewEntity!!.rotationPitch.toDouble()).toFloat())
            .rotateYaw(-Math.toRadians(mc.renderViewEntity!!.rotationYaw.toDouble()).toFloat())

        GL11.glVertex3d(v.x, player.getEyeHeight() + v.y, v.z)
        val x = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks
        val y = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks
        val z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks
        GL11.glVertex3d(x - mc.renderManager.renderPosX, y - mc.renderManager.renderPosY + height / 2.0, z - mc.renderManager.renderPosZ)
        GL11.glEnd()
        GL11.glDepthMask(true)
        GlStateManager.shadeModel(GL11.GL_FLAT)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glPopMatrix()
    }



}