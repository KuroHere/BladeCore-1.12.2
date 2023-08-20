package com.bladecore.client.gui.api.elements

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.IGuiElement
import com.bladecore.client.utils.render.font.Fonts

abstract class Element(val gui: AbstractGui) : IGuiElement {
    val fr get() = Fonts.DEFAULT
}