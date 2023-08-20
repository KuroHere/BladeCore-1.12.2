package com.bladecore.client.gui.api.elements

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.utils.math.Vec2d

abstract class DraggableElement(pos: Vec2d, width: Double, height: Double, gui: AbstractGui) : InteractiveElement(pos, width, height, gui) {
    var isDragging = false

    private var dragX = 0.0
    private var dragY = 0.0

    override fun onGuiOpen() {
        isDragging = false
    }

    override fun onGuiClose() {
        isDragging = false
    }

    override fun onRender() {
        if (!isDragging) return
        pos = gui.mouse.minus(dragX, dragY)
    }

    override fun onMouseAction(action: MouseAction, button: Int) {
        when (action) {
            MouseAction.CLICK -> {
                if (isHovered(gui.mouse) && button == 0){
                    dragX = gui.mouse.x - pos.x
                    dragY = gui.mouse.y - pos.y
                    isDragging = canDrag()
                }
            }

            MouseAction.RELEASE -> {
                isDragging = false
            }
        }
    }

    open fun canDrag(): Boolean { return true }
}