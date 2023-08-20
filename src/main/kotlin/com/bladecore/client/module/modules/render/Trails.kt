package com.bladecore.client.module.modules.render

import com.bladecore.client.event.events.MoveEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.entity.interpolatedPosition
import com.bladecore.client.utils.math.MathUtils.normalize
import com.bladecore.client.utils.render.ColorUtils.glColor
import com.bladecore.client.utils.render.ColorUtils.setAlphaD
import com.bladecore.client.utils.render.GLUtils.draw
import com.bladecore.client.utils.render.GLUtils.glVertex
import com.bladecore.client.utils.render.GLUtils.renderGL
import com.bladecore.client.utils.render.RenderTessellator
import com.bladecore.client.utils.render.RenderUtils3D
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11.*

object Trails : Module(
    "Trails",
    "Draws a walking path",
    Category.RENDER
) {
    private val mode by setting("Mode", Mode.Normal)
    private val length by setting("Length", 10.0, 5.0, 50.0, 1.0)
    private val lineWidth by setting("Width", 1.0, 1.0, 8.0, 1.0)
    private val onlyThirdPerson by setting("Only Third Person", true)

    private val positions = ArrayList<TrailPoint>()

    private var dimension = -100

    private enum class Mode {
        Normal,
        Line
    }

    override fun onEnable() {
        dimension = -100
    }

    init {
        safeListener<MoveEvent> {
            positions.add(TrailPoint(player.positionVector.add(0.0, 0.1, 0.0)))
        }

        listener<TickEvent.ClientTickEvent> {
            positions.removeIf { it.shouldRemove() }
        }

        safeListener<Render3DEvent> {
            if (dimension != player.dimension) {
                dimension = player.dimension
                positions.clear()
                return@safeListener
            }

            if (mc.gameSettings.thirdPersonView == 0 && onlyThirdPerson) return@safeListener

            renderGL {
                glDisable(GL_ALPHA_TEST)
                glLineWidth(lineWidth.toFloat())

                val posList = ArrayList(positions)
                posList.add(TrailPoint(player.interpolatedPosition.add(0.0, 0.1, 0.0)))

                when (mode) {
                    Mode.Normal -> drawNormal(posList)
                    Mode.Line -> drawLine(posList)
                }

                glEnable(GL_ALPHA_TEST)
            }
        }
    }

    private fun drawNormal(trailList: ArrayList<TrailPoint>) {
        // main
        draw(GL_QUAD_STRIP) {
            trailList.forEach { point ->
                point.setColor(0.7)
                vertex(point.pos)
                vertex(point.pos.add(0.0, point.height, 0.0))
            }
        }

        val width = lineWidth * 0.015

        // top line
        draw(GL_QUAD_STRIP) {
            trailList.forEach { point ->
                point.setColor(1.0)
                vertex(point.pos.add(0.0, point.height, 0.0))

                point.setColor(0.0)
                vertex(point.pos.add(0.0, point.height + width, 0.0))
            }
        }

        draw(GL_QUAD_STRIP) {
            trailList.forEach { point ->
                point.setColor(1.0)
                vertex(point.pos.add(0.0, point.height, 0.0))

                point.setColor(0.0)
                vertex(point.pos.add(0.0, point.height - width, 0.0))
            }
        }

        // bottom line
        draw(GL_QUAD_STRIP) {
            trailList.forEach { point ->
                point.setColor(1.0)
                vertex(point.pos)

                point.setColor(0.0)
                vertex(point.pos.add(0.0, width, 0.0))
            }
        }

        draw(GL_QUAD_STRIP) {
            trailList.forEach { point ->
                point.setColor(1.0)
                vertex(point.pos)

                point.setColor(0.0)
                vertex(point.pos.add(0.0, -width, 0.0))
            }
        }
    }

    private fun drawLine(posList: ArrayList<TrailPoint>) {
        draw(GL_LINE_STRIP) {
            posList.forEach { point ->
                point.setColor(1.0)
                vertex(point.pos)
            }
        }
    }

    private fun vertex(vec3d: Vec3d) {
        vec3d.subtract(RenderUtils3D.viewerPos).glVertex()
    }

    private class TrailPoint(val pos: Vec3d) {
        val height = mc.player.getEyeHeight().toDouble() + 0.15
        val initTick = mc.player.ticksExisted

        fun setColor(alpha: Double) {
            HUD.getColor(initTick + mc.player.ticksExisted).setAlphaD(getAlpha() * alpha).glColor()
        }

        fun shouldRemove(): Boolean {
            if (mc.player == null) return true
            if (mc.player.isDead) return true
            return (mc.player.ticksExisted - initTick).toDouble() > length + 10.0
        }

        private fun getAlpha(): Double {
            val i = mc.player.ticksExisted.toDouble() - initTick.toDouble() + RenderTessellator.partialTicks
            return normalize(i, 0.0, length, 1.0, 0.0)
        }
    }
}