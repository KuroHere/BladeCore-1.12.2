package com.bladecore.client.module.modules.hud

import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.ColorUtils.glColor
import com.bladecore.client.utils.render.GLUtils.draw
import com.bladecore.client.utils.render.shader.RectBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL14
import java.awt.Color

object Watermark: DraggableHudModule(
    "Watermark",
    "meow"
) {
    private val size by setting("Size", 1.0, 0.5, 3.0, 0.1)
    private val radius by setting("Radius", 3.0, 0.0, 10.0, 0.5)
    private val lodBiasSetting by setting("Smoothing", 0.0, -10.0, 10.0, 0.5)

    private const val w = 135.0
    private const val h = 20.0
    private const val margin = 4.0

    private val texture = ResourceLocation("bladecore", "icons/logo/modern.png")

    private var lod: Float? = null

    override fun onRender() {
        val pos1 = pos
        val pos2 = pos.plus(getWidth(), getHeight())

        val c1 = HUD.getColor(0)
        val c2 = HUD.getColor(5)

        // background
        RectBuilder(pos1.minus(margin), pos2.plus(margin)).draw {
            color(HUD.bgColor)
            radius(radius)
        }

        // Logo
        drawTexture(pos1, pos2, c1, c2)
    }

    private fun drawTexture(pos1: Vec2d, pos2: Vec2d, color1: Color, color2: Color) {
        mc.textureManager.bindTexture(texture)

        GlStateManager.disableOutlineMode()
        GlStateManager.enableTexture2D()
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()

        glDisable(GL_ALPHA_TEST)

        GlStateManager.resetColor()

        val l = lodBiasSetting.toFloat() * 0.25f - 0.5f
        if (lod != l) {
            glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, l)
            lod = l
        }

        draw(GL_QUADS) {
            color1.glColor()
            glTexCoord2d(0.0, 0.0)
            glVertex3d(pos1.x, pos1.y, 0.0)

            glTexCoord2d(0.0, 1.0)
            glVertex3d(pos1.x, pos2.y, 0.0)

            color2.glColor()
            glTexCoord2d(1.0, 1.0)
            glVertex3d(pos2.x, pos2.y, 0.0)

            glTexCoord2d(1.0, 0.0)
            glVertex3d(pos2.x, pos1.y,0.0)
        }

        GlStateManager.enableAlpha()
        GlStateManager.resetColor()
    }

    override fun getWidth() = w * size * 0.65
    override fun getHeight() = h * size * 0.65
}