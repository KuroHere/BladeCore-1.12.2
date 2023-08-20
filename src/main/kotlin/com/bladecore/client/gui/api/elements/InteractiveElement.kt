package com.bladecore.client.gui.api.elements

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.HoverUtils

abstract class InteractiveElement(pos: Vec2d, width: Double, height: Double, gui: AbstractGui) : RectElement(pos, width, height, gui) {
    protected val hovered get() = isHovered(gui.mouse)
    protected open fun isHovered(mousePos: Vec2d): Boolean { return HoverUtils.isHovered(mousePos, pos, pos.plus(width, height)) }
}