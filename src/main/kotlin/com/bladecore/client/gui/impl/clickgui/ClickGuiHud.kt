package com.bladecore.client.gui.impl.clickgui

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.gui.impl.clickgui.elements.CategoryPanel
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.utils.math.Vec2d
import org.lwjgl.input.Mouse

class ClickGuiHud : AbstractGui() {
    var currentScale = 1.0; private set
    override fun getScaleFactor() = currentScale

    val panels = ArrayList<CategoryPanel>()

    var dWheel = 0.0; private set

    override fun onRegister() =
        Module.Category.values().forEachIndexed { index, category ->
            val panel = CategoryPanel(Vec2d(3.0 + index.toDouble() * (ClickGui.width + 3.0), 5.0), 0.0, 0.0, this, category)
            panel.onRegister()
            panels.add(panel)
        }

    override fun onGuiOpen() = panels.forEach { it.onGuiOpen() }.also { currentScale = ClickGui.scale }
    override fun onGuiClose() = panels.forEach { it.onGuiClose() }

    override fun onKey(typedChar: Char, key: Int) = super.onKey(typedChar, key).also { panels.forEach { it.onKey(typedChar, key) } }
    override fun onMouseAction(action: MouseAction, button: Int) {
        if (action == MouseAction.CLICK) {
            panels.lastOrNull { it.panelHovered }?.let { panel ->
                panels.remove(panel)
                panels.add(panel)
            }
        }

        panels.forEach { panel ->
            if (panel == panels.lastOrNull { it.panelHovered } || action == MouseAction.RELEASE)
                panel.onMouseAction(action, button)
        }
    }

    override fun onTick() = panels.forEach { it.onTick() }
    override fun onRender() {
        dWheel = Mouse.getDWheel().toDouble()
        panels.forEach { it.onRender() }
    }

    fun isPanelFocused(panel: CategoryPanel): Boolean {
        if (panel == panels.lastOrNull { it.panelHovered }) return true

        val index = panels.indexOf(panel)
        val hoveredPanels = panels.filter { it.panelHovered }
        return hoveredPanels.all { panels.indexOf(it) < index }
    }
}
