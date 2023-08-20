package com.bladecore.client.module.modules.render

import com.bladecore.client.event.events.AttackEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.listener.Nameable
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.MathUtils.lerp
import com.bladecore.client.utils.math.MathUtils.toIntSign
import com.bladecore.client.utils.render.ColorUtils.glColor
import com.bladecore.client.utils.render.ColorUtils.setAlpha
import com.bladecore.client.utils.render.GLUtils.draw
import com.bladecore.client.utils.render.GLUtils.matrix
import com.bladecore.client.utils.render.GLUtils.renderGL
import com.bladecore.client.utils.render.RenderUtils3D
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object HitParticles : Module(
    "HitParticles",
    "Spawns particles by hitting an entity",
    Category.RENDER
) {
    private val mode by setting("Draw Mode", Mode.CircleOne)
    private val size by setting("Size", 1.0, 0.5, 2.0, 0.05)
    private val amount by setting("Amount", 25.0, 3.0, 50.0, 1.0)
    private val maxAmount by setting("Max Amount", 250.0, 100.0, 500.0, 1.0)
    private val customHeight by setting("Custom Height", false)
    private val spawnHeight by setting("Spawn Height", 0.5, 0.0, 1.0, 0.1)
    private val speedH by setting("Speed H", 1.0, 0.1, 3.0, 0.1)
    private val speedV by setting("Speed V", 0.5, 0.1, 3.0, 0.1)
    private val duration by setting("Duration", 30.0, 5.0, 50.0, 0.2)
    private val inertiaAmount by setting("Inertia Amount", 0.8, 0.0, 1.0, 0.05)
    private val gravityAmount by setting("Gravity Amount", 0.0, 0.0, 1.0, 0.05)

    private val particles = ArrayList<HitParticle>()

    private enum class Mode(override val displayName: String) : Nameable {
        CircleOne("Circle 1"),
        CircleTwo("Circle 2"),
        Bubble("Bubble")
    }

    init {
        safeListener<AttackEvent.Pre> {
            for (i in 0..amount.toInt()) {
                val height = if (customHeight) spawnHeight else i.toDouble() / amount

                val pos = it.entity.positionVector.add(0.0, it.entity.height * height, 0.0)
                val particle = HitParticle(pos, particles.count())
                particles.add(particle)
            }
        }

        safeListener<TickEvent.ClientTickEvent> { event ->
            if (event.phase != TickEvent.Phase.START) return@safeListener
            particles.forEach { it.tick() }
            particles.removeIf { it.livingTicks > it.maxLivingTicks }
            while (particles.count() > maxAmount) { particles.removeAt(0) }
        }

        safeListener<Render3DEvent> {
            renderGL {
                particles.forEach { it.draw() }
            }
        }
    }

    override fun onEnable() =
        particles.clear()

    private class HitParticle(posIn: Vec3d, val index: Int) {
        private var prevPos = Vec3d.ZERO
        var pos: Vec3d = Vec3d.ZERO

        private var motionX = 0.0
        private var motionY = 0.0
        private var motionZ = 0.0

        var livingTicks = 0
        var maxLivingTicks = 100

        init {
            prevPos = posIn
            pos = posIn

            val r = Random()
            // -0.1..0.1 * speed
            motionX = (r.nextDouble() - 0.5) * 0.2 * speedH
            motionY = (r.nextDouble() - 0.5) * 0.2 * speedV
            motionZ = (r.nextDouble() - 0.5) * 0.2 * speedH

            maxLivingTicks = (duration * 10.0 + r.nextDouble() * 40.0).toInt()
        }

        fun tick() {
            livingTicks++

            prevPos = pos
            pos = pos.add(motionX, motionY, motionZ)

            motionY -= gravityAmount * 0.01

            motionX *= 0.9 + (inertiaAmount / 10.0)
            motionY *= 0.9 + (inertiaAmount / 10.0)
            motionZ *= 0.9 + (inertiaAmount / 10.0)
        }

        fun draw() {
            val interpolatedPos = lerp(prevPos, pos, mc.renderPartialTicks.toDouble())
            val p = interpolatedPos.subtract(RenderUtils3D.viewerPos)

            matrix {
                glTranslated(p.x, p.y, p.z)
                glNormal3f(0.0f, 1.0f, 0.0f)
                glRotatef(-mc.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                glRotatef((mc.gameSettings.thirdPersonView != 2).toIntSign().toFloat() * mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)

                glScaled(-0.005 * size, -0.005 * size, 0.005 * size)

                val p1 = min(livingTicks.toDouble(), 10.0) / 10.0
                val p2 = clamp((maxLivingTicks - livingTicks).toDouble() / 10.0, 0.0, 1.0)
                val alpha = ((if (livingTicks <= 10) p1 else p2) * 255.0).toInt()

                val c1 = HUD.getColor(index)
                val c2 = HUD.getColor(index, 0.6)
                val c3 = HUD.getColor(index, 0.3)

                val dist = RenderUtils3D.viewerPos.distanceTo(pos)
                val s = clamp(16 / dist, 5.0, 16.0).toInt()

                when (mode) {
                    Mode.CircleOne -> {
                        if (dist < 6) circle(15f, c3.setAlpha((alpha * 0.2).toInt()), s)
                        if (dist < 8) circle(10f, c2.setAlpha((alpha * 0.7).toInt()), s)
                        circle(7f, c1.setAlpha(alpha), s)
                    }
                    Mode.CircleTwo -> {
                        circle(15f, c3.setAlpha(alpha), s)
                        circle(7f, c1.setAlpha(alpha), s)
                    }
                    Mode.Bubble -> {
                        circle(12f, c1.setAlpha(alpha), s)
                        circle(10f, c2.setAlpha(alpha), s)
                    }
                }
            }
        }
    }

    private fun circle(radius: Float, color: Color, sections: Int) {
        val dAngle = 2 * Math.PI / sections
        color.glColor()

        draw(GL_TRIANGLE_FAN) {
            for (i in 0 until sections) {
                glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
                glVertex2d(radius * sin(i * dAngle), radius * cos(i * dAngle))
            }
        }

        GlStateManager.resetColor()
    }
}