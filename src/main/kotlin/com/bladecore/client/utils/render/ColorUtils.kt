package com.bladecore.client.utils.render

import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.MathUtils.lerp
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.glColor4f
import java.awt.Color
import kotlin.math.ceil

object ColorUtils{
    val Color.r get() = red.toFloat() / 255f
    val Color.g get() = green.toFloat() / 255f
    val Color.b get() = blue.toFloat() / 255f
    val Color.a get() = alpha.toFloat() / 255f

    fun Color.glColor() {
        glColor4f(this.red / 255f, this.green / 255f, this.blue / 255f, this.alpha / 255f)
    }

    fun Color.setAlphaD(amount: Double): Color {
        return this.setAlpha((clamp(amount, 0.0, 1.0) * 255.0).toInt())
    }

    fun Color.multAlpha(amount: Double): Color {
        return this.setAlpha(clamp(amount * this.alpha.toDouble(), 0.0, 255.0).toInt())
    }

    fun Color.setAlpha(amount: Int): Color {
        return Color(this.red, this.green, this.blue, amount)
    }

    val logo = ResourceLocation("bladecore", "icons/logo/menu.png")


    val icon_singleplayer = ResourceLocation("bladecore", "icons/singleplayer.png")
    val icon_multiplayer = ResourceLocation("bladecore", "icons/multiplayer.png")
    val icon_altmanager = ResourceLocation("bladecore", "icons/altmanager.png")
    val icon_settings = ResourceLocation("bladecore", "icons/settings.png")
    val icon_shutdown = ResourceLocation("bladecore", "icons/shutdown.png")

    fun lerp(c1: Color, c2: Color, p: Double): Color {
        val r = lerp(c1.r, c2.r, p.toFloat())
        val g = lerp(c1.g, c2.g, p.toFloat())
        val b = lerp(c1.b, c2.b, p.toFloat())
        val a = lerp(c1.a, c2.a, p.toFloat())

        return Color(r, g, b, a)
    }
}