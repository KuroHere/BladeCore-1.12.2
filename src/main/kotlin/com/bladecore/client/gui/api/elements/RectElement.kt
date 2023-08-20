package com.bladecore.client.gui.api.elements

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.utils.math.Vec2d

abstract class RectElement(var pos: Vec2d, var width: Double, var height: Double, gui: AbstractGui): Element(gui)