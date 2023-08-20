package com.bladecore.client.module

import com.bladecore.client.event.events.Render2DEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.utils.render.GLUtils.matrix

abstract class HudModule(
    name: String,
    description: String,
    alwaysListenable: Boolean = false
) : Module(name, description, Category.HUD, alwaysListenable = alwaysListenable) {
    init {
        safeListener<Render2DEvent>(10) {
            if (this@HudModule is DraggableHudModule) onRenderPre()
            matrix {
                onRender()
            }
            if (this@HudModule is DraggableHudModule) onRenderPost()
        }
    }

    open fun onRender() {}
}