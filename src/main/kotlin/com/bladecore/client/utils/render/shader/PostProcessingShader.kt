package com.bladecore.client.utils.render.shader

import com.bladecore.BladeCore
import com.bladecore.client.event.EventBus
import com.bladecore.client.event.events.ResolutionUpdateEvent
import com.bladecore.client.event.listener.listener
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.client.shader.ShaderLinkHelper
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11

class PostProcessingShader(shaderIn: ResourceLocation, frameBufferNames: List<String>) {
    private val mc = Minecraft.getMinecraft()

    val shader: ShaderGroup?
    private val frameBufferMap = HashMap<String, Framebuffer>()
    private var frameBuffersInitialized = false

    init {
        shader = when {
            !OpenGlHelper.shadersSupported -> {
                BladeCore.LOG.warn("Shaders are unsupported by OpenGL!")
                null
            }

            isIntegratedGraphics -> {
                BladeCore.LOG.warn("Shaders are unsupported by Intel Integrated Graphics!")
                null
            }

            else -> {
                try {
                    ShaderLinkHelper.setNewStaticShaderLinkHelper()

                    ShaderGroup(mc.textureManager, mc.resourceManager, mc.framebuffer, shaderIn).also {
                        it.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
                    }
                } catch (e: Exception) {
                    BladeCore.LOG.warn("Failed to load shaders")
                    e.printStackTrace()

                    null
                }?.also {
                    frameBufferNames.forEach { name -> frameBufferMap[name] = it.getFramebufferRaw(name) }
                }
            }

        }

        listener<TickEvent.ClientTickEvent> {
            if (frameBuffersInitialized) return@listener

            shader?.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
            frameBuffersInitialized = true

        }

        listener<ResolutionUpdateEvent> {
            shader?.createBindFramebuffers(it.width, it.height)
        }

        EventBus.subscribe(this)
    }

    fun getFrameBuffer(name: String) = frameBufferMap[name]

    companion object {
        val isIntegratedGraphics by lazy {
            GlStateManager.glGetString(GL11.GL_VENDOR).contains("Intel")
        }
    }
}