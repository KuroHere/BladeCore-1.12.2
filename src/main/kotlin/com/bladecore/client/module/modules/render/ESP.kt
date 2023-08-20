package com.bladecore.client.module.modules.render

import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.events.Render3DEvent
import com.bladecore.client.event.events.RenderEntityEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.extension.entity.getInterpolatedBox
import com.bladecore.client.utils.extension.entity.interpolatedPosition
import com.bladecore.client.utils.extension.settingName
import com.bladecore.client.utils.player.TargetingUtils.getTargetList
import com.bladecore.client.utils.render.ColorUtils.setAlpha
import com.bladecore.client.utils.render.GLUtils.renderGL
import com.bladecore.client.utils.render.RenderUtils3D.viewerPos
import com.bladecore.client.utils.render.esp.ESPRenderer
import com.bladecore.client.utils.render.shader.PostProcessingShader
import com.bladecore.client.utils.threads.runAsync
import com.bladecore.mixin.accessor.AccessorRenderManager
import com.bladecore.mixin.accessor.render.AccessorShaderGroup
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11
import java.awt.Color

object ESP : Module(
    "ESP",
    "Highlights entity's",
    Category.RENDER
) {
    private val page by setting("Page", Page.Render)

    private val mode by setting("Mode", Mode.Shader, { page == Page.Render })
    private val lineWidth by setting("Line Width", 1.0, 1.0, 8.0, 0.1, { page ==  Page.Render })
    private val blurRadius by setting("Blur Radius", 0.0, 0.0, 16.0, 1.0, { page ==  Page.Render && mode == Mode.Shader })
    private val showOriginal by setting("Show Original", false, { page ==  Page.Render && mode == Mode.Shader })
    private val color by setting("Color", Color(80, 200, 200, 250), { page ==  Page.Render })
    private val filledAlpha by setting("Filled Alpha", 0.4, 0.0, 1.0, 0.05, { page ==  Page.Render })
    private val outlineAlpha by setting("Outline Alpha", 0.4, 0.0, 1.0, 0.05, { page ==  Page.Render })

    private val range by setting("Range", 32.0, 8.0, 256.0, 1.0, { page == Page.Targets })
    private val self by setting("Self", false, { page == Page.Targets })
    private val players by setting("Players", true, { page == Page.Targets })
    private val friends by setting("Friends", true, { page == Page.Targets && players })
    private val hostiles by setting("Hostiles", false, { page == Page.Targets })
    private val animals by setting("Animals", false, { page == Page.Targets })
    private val crystals by setting("Crystals", false, { page == Page.Targets })

    private val renderer = ESPRenderer()
    private val entityList = LinkedHashSet<Entity>()

    private val shader = PostProcessingShader(ResourceLocation("shaders/post/esp_outline.json"), listOf("final"))
    private val buffer = shader.getFrameBuffer("final")

    override fun getHudInfo() = mode.settingName

    private enum class Page {
        Render,
        Targets
    }

    private enum class Mode {
        Shader,
        Box
    }

    init {
        safeListener<RenderEntityEvent.Pre> {
            if (entityList.isEmpty()) return@safeListener
            if (mode == Mode.Shader &&
                !(mc.renderManager as AccessorRenderManager).renderOutlines
                && !showOriginal
                && entityList.contains(it.entity)
                && it.entity != player) {
                it.cancel()
            }
        }

        safeListener<Render3DEvent> {
            when (mode) {
                Mode.Box -> drawBox()
                Mode.Shader -> renderGL { drawShader() }
            }
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.START) return@safeListener

            updateEntityList()

            if (player.ticksExisted % 3 != 0) return@safeListener
            runAsync {
                updateShader()
            }
        }
    }

    private fun SafeClientEvent.updateEntityList() {
        entityList.clear()
        entityList.addAll(getTargetList(players, friends, hostiles, animals, true))
        if (crystals) entityList.addAll(world.loadedEntityList.filterIsInstance<EntityEnderCrystal>())
        if (self && mc.gameSettings.thirdPersonView != 0) entityList.add(player)
        entityList.removeIf { it.positionVector.distanceTo(player.positionVector) > range }
    }

    private fun drawBox() {
        entityList.forEach { entity ->
            renderer.put(
                ((entity as? EntityLivingBase)?.getInterpolatedBox() ?: entity.entityBoundingBox),
                color.setAlpha((filledAlpha * 255).toInt()),
                color.setAlpha((outlineAlpha * 255).toInt()),
            )
        }

        renderer.thickness = lineWidth.toFloat()
        renderer.render()
    }

    private fun drawShader() {
        if (entityList.isEmpty()) return
        // Clean up the frame buffer and bind it
        buffer?.framebufferClear()
        buffer?.bindFramebuffer(false)

        val prevRenderOutlines = (mc.renderManager as AccessorRenderManager).renderOutlines

        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()

        // Draw the entities into the framebuffer
        entityList.forEach { entity ->
            val renderer = mc.renderManager.getEntityRenderObject<Entity>(entity) ?: return@forEach

            val partialTicks = mc.renderPartialTicks
            val yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
            val pos = ((entity as? EntityLivingBase)?.interpolatedPosition ?: entity.positionVector).subtract(viewerPos)

            renderer.setRenderOutlines(true)
            renderer.doRender(entity, pos.x, pos.y, pos.z, yaw, partialTicks)
            renderer.setRenderOutlines(prevRenderOutlines)
        }

        GlStateManager.disableTexture2D()

        // Push matrix
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.pushMatrix()
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.pushMatrix()

        shader.shader?.render(mc.renderPartialTicks)

        // Re-enable blend because shader rendering will disable it at the end
        GlStateManager.enableBlend()
        GlStateManager.disableDepth()

        // Draw it on the main frame buffer
        mc.framebuffer.bindFramebuffer(false)
        buffer?.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false)

        // Revert states
        GlStateManager.enableBlend()
        GlStateManager.enableDepth()
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)
        GlStateManager.disableCull()

        // Revert matrix
        GlStateManager.matrixMode(GL11.GL_PROJECTION)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(GL11.GL_MODELVIEW)
        GlStateManager.popMatrix()
    }

    private fun updateShader() {
        val group = shader.shader ?: return
        val shaders = (group as AccessorShaderGroup).listShaders ?: return

        shaders.forEach { shader ->
            shader.shaderManager.getShaderUniform("color")?.set(color.red / 255f, color.green / 255f, color.blue / 255f)
            shader.shaderManager.getShaderUniform("filledAlpha")?.set(filledAlpha.toFloat())
            shader.shaderManager.getShaderUniform("outlineAlpha")?.set(outlineAlpha.toFloat())
            shader.shaderManager.getShaderUniform("width")?.set(lineWidth.toFloat())
            shader.shaderManager.getShaderUniform("Radius")?.set(blurRadius.toFloat())
        }
    }
}