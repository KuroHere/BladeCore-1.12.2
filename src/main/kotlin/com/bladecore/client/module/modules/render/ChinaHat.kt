package com.bladecore.client.module.modules.render

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.entity.interpolatedPosition
import com.bladecore.client.utils.extension.mixins.timer
import com.bladecore.client.utils.math.MathUtils.toInt
import com.bladecore.client.utils.render.ColorUtils.glColor
import com.bladecore.client.utils.render.ColorUtils.setAlpha
import com.bladecore.client.utils.render.GLUtils.draw
import com.bladecore.client.utils.render.GLUtils.matrix
import com.bladecore.client.utils.render.GLUtils.renderGL
import com.bladecore.client.utils.render.GLUtils.translateGL
import com.bladecore.client.utils.render.RenderUtils3D
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11.*
import kotlin.math.cos
import kotlin.math.sin

object ChinaHat : Module(
    "ChinaHat",
    "China hat over your had",
    Category.RENDER
) {
    private val self by setting("Self", true)
    private val other by setting("Other", false)

    private val heightValue by setting("Height", 0.3, 0.0, 0.7, 0.05)
    private val radiusValue by setting("Radius", 1.0, 0.3, 2.0, 0.05)
    private val rotateSpeed by setting("Rotate Speed", 2.0, 0.0, 10.0, 0.1)
    private val alpha1 by setting("Alpha Top", 0.6, 0.1, 1.0, 0.05)
    private val alpha2 by setting("Alpha Bottom", 0.3, 0.1, 1.0, 0.05)
    private val outline by setting("Outline", false)
    private val outlineWidth by setting("Outline Width", 2.0, 1.0, 5.0, 0.1, { outline })
    private val outlineAlpha by setting("Outline Alpha", 0.6, 0.1, 1.0, 0.05, { outline })

    init {
        safeListener<Render3DEvent> {
            renderGL {
                glLineWidth(outlineWidth.toFloat())
                if (mc.gameSettings.thirdPersonView != 0 && !player.isElytraFlying && self) draw(player)

                world.loadedEntityList.filterIsInstance<EntityPlayer>().filter { it != player && other }.forEach {
                    draw(it)
                }
            }
        }
    }

    private fun SafeClientEvent.draw(entity: EntityLivingBase) {
        matrix {
            entity.interpolatedPosition
                .subtract(RenderUtils3D.viewerPos)
                .add(0.0, entity.height + 0.1, 0.0)
                .subtract(0.0, entity.isSneaking.toInt().toDouble() * 0.1, 0.0)
                .translateGL()

            glRotated((entity.ticksExisted + mc.timer.renderPartialTicks) * -rotateSpeed, 0.0, 1.0, 0.0)
            glRotated(-player.rotationYaw.toDouble(), 0.0, 1.0, 0.0)

            val radius = (entity.entityBoundingBox.maxX - entity.entityBoundingBox.minX) * 0.9 * radiusValue

            drawHat(radius)
            if (outline) drawOutline(radius)
        }
    }

    private fun drawHat(radius: Double) {
        glDisable(GL_CULL_FACE)

        draw(GL_TRIANGLE_FAN) {
            HUD.getColor().setAlpha((alpha1 * 255.0).toInt()).glColor()
            glVertex3d(0.0, heightValue, 0.0)

            for (i in 0..360 step 5) {
                val p = i.toDouble() / 360.0
                val colorProgress = (if (p > 0.5) 1.0 - p else p) * 2.0
                HUD.getColorByProgress(colorProgress).setAlpha((alpha2 * 255.0).toInt()).glColor()

                val dir = Math.toRadians(i - 180.0)
                val x = -sin(dir) * radius
                val z = cos(dir) * radius
                glVertex3d(x, 0.0, z)
            }

            glVertex3d(0.0, heightValue, 0.0)
        }

        glEnable(GL_CULL_FACE)
    }

    private fun drawOutline(radius: Double) {
        draw(GL_LINE_STRIP) {
            for (i in 0..360 step 5) {
                val p = i.toDouble() / 360.0
                val colorProgress = (if (p > 0.5) 1.0 - p else p) * 2.0
                HUD.getColorByProgress(colorProgress).setAlpha((outlineAlpha * 255.0).toInt()).glColor()

                val dir = Math.toRadians(i - 180.0)
                val x = -sin(dir) * radius
                val z = cos(dir) * radius
                glVertex3d(x, 0.0, z)
            }
        }
    }
}