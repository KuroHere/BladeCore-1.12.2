package com.bladecore.client.utils.render

import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.ColorUtils.glColor
import com.bladecore.client.utils.render.GLUtils.draw
import com.bladecore.client.utils.render.GLUtils.glColor
import com.bladecore.client.utils.render.GLUtils.matrix
import com.jhlabs.image.GaussianFilter
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec2f
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.*

object RenderUtils2D {
    private val blurCache = HashMap<BlurData, Int>()
    private val mc = Minecraft.getMinecraft()

    fun drawItem(itemStack: ItemStack, x: Double, y: Double, text: String? = null, drawOverlay: Boolean = true) {
        glPushMatrix()

        GlStateManager.enableBlend()
        GlStateManager.enableDepth()

        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.translate(x, y, 0.0)

        mc.renderItem.zLevel = 0.0f
        mc.renderItem.renderItemAndEffectIntoGUI(itemStack, 0, 0)
        if (drawOverlay) mc.renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, 0, 0, text)
        mc.renderItem.zLevel = 0.0f

        RenderHelper.disableStandardItemLighting()
        GlStateManager.translate(-x, -y, 0.0)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableDepth()
        GlStateManager.enableTexture2D()
        glPopMatrix()
    }

    fun drawRect(pos1: Vec2d, pos2: Vec2d, color: Color) {
        drawRect(Vec2f(pos1.x.toFloat(), pos1.y.toFloat()), Vec2f(pos2.x.toFloat(), pos2.y.toFloat()), color)
    }

    private fun drawRect(pos1: Vec2f, pos2: Vec2f, color: Color) {
        var x1 = pos1.x
        var y1 = pos1.y
        var x2 = pos2.x
        var y2 = pos2.y

        if (x1 < x2) {
            val i = x1
            x1 = x2
            x2 = i
        }
        if (y1 < y2) {
            val j = y1
            y1 = y2
            y2 = j
        }

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        glColor(color)
        glBegin(GL_QUADS)
        glVertex2d(x2.toDouble(), y1.toDouble())
        glVertex2d(x1.toDouble(), y1.toDouble())
        glVertex2d(x1.toDouble(), y2.toDouble())
        glVertex2d(x2.toDouble(), y2.toDouble())
        glEnd()
        GlStateManager.resetColor()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    fun drawGradientRect(posBegin: Vec2d, posEnd: Vec2d, colorLeftTop: Color, colorRightTop: Color, colorLeftBottom: Color, colorRightBottom: Color) {
        drawGradientRect(posBegin.toVec2f(), posEnd.toVec2f(), colorLeftTop, colorRightTop, colorLeftBottom, colorRightBottom)
    }

    private fun drawGradientRect(pos1: Vec2f, pos2: Vec2f, color1: Color, color2: Color, color3: Color, color4: Color) {
        val x1 = min(pos1.x, pos2.x)
        val y1 = min(pos1.y, pos2.y)
        val x2 = max(pos1.x, pos2.x)
        val y2 = max(pos1.y, pos2.y)

        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glHint(3154, 4354)
        glHint(3155, 4354)

        glShadeModel(7425)
        glBegin(7)

        color1.glColor()
        glVertex2f(x1, y2)

        color2.glColor()
        glVertex2f(x2, y2)

        color3.glColor()
        glVertex2f(x2, y1)

        color4.glColor()
        glVertex2f(x1, y1)

        glEnd()
        glShadeModel(7424)

        glHint(3154, 4352)
        glHint(3155, 4352)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

    }

    fun drawImage(image: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        mc.textureManager.bindTexture(image)
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, width, height, width.toFloat(), height.toFloat())
    }

    fun drawLine(start: Vec2d, end: Vec2d, width: Float, color: Color) {
        glDisable(GL_TEXTURE_2D)
        glLineWidth(width)
        glBegin(GL_LINES)
        glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
        glVertex2d(start.x, start.y)
        glVertex2d(end.x, end.y)
        glEnd()
        glEnable(GL_TEXTURE_2D)
    }

    fun drawBlurredRect(posBegin: Vec2d, posEnd: Vec2d, blurRadius: Int, color: Color){
        val x = min(posBegin.x, posEnd.x).toFloat()
        val y = min(posBegin.y, posEnd.y).toFloat()
        val width = max(posBegin.x, posEnd.x).toFloat() - x
        val height = max(posBegin.y, posEnd.y).toFloat() - y

        drawBlurredShadow(x, y, width, height, blurRadius, color)
    }

    private data class BlurData(val width: Float, val height: Float, val blurRadius: Int){
        override fun equals(other: Any?): Boolean {
            if (other !is BlurData) return false

            return width == other.width &&
                height == other.height &&
                blurRadius == other.blurRadius
        }

        override fun hashCode(): Int {
            var result = width.hashCode()
            result = 31 * result + height.hashCode()
            result = 31 * result + blurRadius
            return result
        }
    }

    private fun drawBlurredShadow(xIn: Float, yIn: Float, widthIn: Float, heightIn: Float, blurRadiusIn: Int, colorIn: Color) {
        matrix {
            GlStateManager.alphaFunc(GL_GREATER, 0.01f)

            val x = xIn - blurRadiusIn
            val y = yIn - blurRadiusIn

            val width = widthIn + blurRadiusIn * 2
            val height = heightIn + blurRadiusIn * 2

            val id = BlurData(widthIn, heightIn, blurRadiusIn)

            GlStateManager.enableTexture2D()
            glDisable(GL_CULL_FACE)
            glDisable(GL_ALPHA_TEST)
            GlStateManager.enableBlend()

            val texture = blurCache[id] ?: run {
                val w = max(1, width.toInt())
                val h = max(1, height.toInt())
                val image = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE)

                image.graphics.apply {
                    color = Color.WHITE
                    fillRect(blurRadiusIn, blurRadiusIn, (w - blurRadiusIn * 2), (h - blurRadiusIn * 2))
                    dispose()
                }

                val blurred = GaussianFilter(blurRadiusIn.toFloat()).filter(image, null)
                val textureId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false)
                blurCache[id] = textureId

                textureId
            }

            GlStateManager.bindTexture(texture)

            colorIn.glColor()

            draw(GL_QUADS) {
                glTexCoord2f(0f, 0f) // top left
                glVertex2f(x, y)

                glTexCoord2f(0f, 1f) // bottom left
                glVertex2f(x, y + height)

                glTexCoord2f(1f, 1f) // bottom right
                glVertex2f(x + width, y + height)

                glTexCoord2f(1f, 0f) // top right
                glVertex2f(x + width, y)
            }

            GlStateManager.disableBlend()
            GlStateManager.resetColor()

            glEnable(GL_CULL_FACE)
        }
    }
}