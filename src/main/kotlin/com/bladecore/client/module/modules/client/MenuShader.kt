package com.bladecore.client.module.modules.client

import com.bladecore.client.module.Module
import com.bladecore.client.utils.render.shader.Shaders
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11

object MenuShader : Module(
    "MenuShader",
    "Potato pc killer",
    Category.CLIENT,
) {
    var initTime: Long = 0x22

    private val timeUniform by lazy { Shaders.menuShader.getUniform("time") }
    private val mouseUniform by lazy { Shaders.menuShader.getUniform("mouse") }
    private val resolutionUniform by lazy { Shaders.menuShader.getUniform("resolution") }

    @JvmStatic
    fun draw() {
        val width = mc.displayWidth.toFloat()
        val height = mc.displayHeight.toFloat()
        val mouseX = Mouse.getX() - 1.0f
        val mouseY = height - Mouse.getY() - 1.0f

        with(Shaders.menuShader) {
            begin()

            uniformf(timeUniform, ((System.currentTimeMillis() - initTime) / 1000.0).toFloat())
            uniformf(mouseUniform, mouseX / width, (height - 1.0f - mouseY) / height)
            uniformf(resolutionUniform, width, height)

            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer

            with(buffer) {
                begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

                pos(-1.0, -1.0, 0.0)
                endVertex()

                pos(1.0, -1.0, 0.0)
                endVertex()

                pos(1.0, 1.0, 0.0)
                endVertex()

                pos(-1.0, 1.0, 0.0)
                endVertex()
            }

            tessellator.draw()

            end()
        }
    }
}