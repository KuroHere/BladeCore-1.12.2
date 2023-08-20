package com.bladecore.client.gui.impl.clickgui.elements

import com.bladecore.client.gui.api.AbstractGui
import com.bladecore.client.gui.api.elements.DraggableElement
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.gui.impl.clickgui.ClickGuiHud
import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.ClickGui
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.utils.extension.transformIf
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.MathUtils.lerp
import com.bladecore.client.utils.math.MathUtils.toInt
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.ColorUtils.setAlphaD
import com.bladecore.client.utils.render.GLUtils
import com.bladecore.client.utils.render.HoverUtils
import com.bladecore.client.utils.render.ScissorUtils.scissor
import com.bladecore.client.utils.render.ScissorUtils.toggleScissor
import com.bladecore.client.utils.render.font.Fonts
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import com.bladecore.client.utils.render.shader.RectBuilder
import kotlin.math.max
import kotlin.math.sign

class CategoryPanel(pos: Vec2d, width: Double, height: Double, gui: AbstractGui, val category: Module.Category) : DraggableElement(pos, width, height, gui) {
    override fun onRegister() {
        modules.addAll(ModuleManager.getModules().filter { it.category == category }.map { ModuleButton(it, 0, gui as ClickGuiHud, this) })
        modules.forEach { it.onRegister() }
    }
    override fun onGuiCloseAttempt() {}

    override fun onGuiOpen() = super.onGuiOpen().also {
        isDraggingHeight = false
        modules.forEach { it.onGuiOpen() }
        windowHeight = 0.0
    }
    override fun onGuiClose() = super.onGuiClose().also { modules.forEach { it.onGuiClose() } }

    val modules = ArrayList<ModuleButton>()

    var yRange = 0.0 to 0.0; private set

    private var scrollSpeed = 0.0
    private var scrollShift = 0.0

    private var targetWindowHeight = 320.0
    private var windowHeight = 0.0

    private var extended = true

    private var lastClickTime = 0L

    private var modulesHovered = false
    var panelHovered = false; private set
    var panelFocused = false; private set

    private var draggingHovered = false
    private var isDraggingHeight = false
    private var dragPos = 0.0

    override fun onTick() {
        when (ClickGui.sorting) {
            ClickGui.SortingMode.Alphabetical -> modules.sortBy { it.module.name }
            ClickGui.SortingMode.ByWidth -> modules.sortBy { -fr.getStringWidth(it.module.name, ClickGui.fontSize) }
        }

        if (ClickGui.reverse) modules.reverse()

        modules.filter { extended }.forEach { it.onTick() }
    }

    override fun onRender() {
        super.onRender()

        if (isDraggingHeight)
            targetWindowHeight = max(gui.mouse.y - dragPos, 100.0)

        windowHeight = lerp(windowHeight, targetWindowHeight.transformIf(!extended) { 0.0 }, GLUtils.deltaTimeDouble() * 5.0 * ClickGui.resizeSpeed)
        scroll()

        width = ClickGui.width
        height = ClickGui.height

        updateModules()

        val radius = ClickGui.panelRound

        RectBuilder(pos, pos.plus(width, height + windowHeight)).color(ClickGui.backgroundColor).radius(radius).draw()

        val textPos = pos.plus(width / 2.0 - Fonts.DEFAULT_BOLD.getStringWidth(category.displayName, ClickGui.titleFontSize) / 2.0, height / 2.0)
        Fonts.DEFAULT_BOLD.drawString(category.displayName, textPos, scale = ClickGui.titleFontSize, color = HUD.getColor(-1))

        val p1 = pos.plus(0.0, height)
        val p2 = pos.plus(width, height + windowHeight)

        yRange = (pos.y + height) to (pos.y + height + max(0.0, windowHeight - radius))
        modulesHovered = HoverUtils.isHovered(gui.mouse, p1, p2) && extended
        panelHovered = modulesHovered || hovered
        panelFocused = (gui as ClickGuiHud).isPanelFocused(this)
        draggingHovered = HoverUtils.isHovered(gui.mouse, pos.plus(0.0, height + windowHeight - radius), pos.plus(width, height + windowHeight + radius)) && extended

        toggleScissor(true)
        scissor(p1, p2.minus(0.0, radius), gui.currentScale * 2.0) {
            modules.forEach {
                if (!checkCulling(it.pos, it.pos.plus(it.width, it.getButtonHeight()), p1, p2)) return@forEach
                it.onRender()
            }
        }
        toggleScissor(false)

        val dragText = "..."
        val dragTextPos = pos.plus(width / 2.0 - Fonts.DEFAULT_BOLD.getStringWidth(dragText) * 0.5, height + windowHeight - radius)
        Fonts.DEFAULT_BOLD.drawString(dragText, dragTextPos, color = HUD.getColor(-1).setAlphaD(windowHeight / targetWindowHeight))
    }

    private fun scroll() {
        val t = GLUtils.deltaTimeDouble()

        scrollSpeed += sign((gui as ClickGuiHud).dWheel) * 150.0 * ClickGui.scrollSpeed * (extended && modulesHovered && panelFocused && !isDraggingHeight).toInt()

        val scrollDistance = scrollSpeed * t
        scrollShift += scrollDistance

        val min = (-modules.sumOf { it.getButtonHeight() }) * extended.toInt().toDouble()
        val max = 0.0
        scrollShift = lerp(scrollShift, clamp(scrollShift, min, max), 5.0 * t)

        val decay = 0.6 + ClickGui.scrollDecay * 0.3
        scrollSpeed *= decay
    }

    private fun updateModules() {
        val x = pos.x
        var y = pos.y + height
        modules.forEachIndexed { index, it ->
            it.index = index
            it.pos = Vec2d(x, y + scrollShift)
            it.width = width
            it.height = height
            it.update()
            y += it.getButtonHeight()
        }
    }

    private fun checkCulling(pos1: Vec2d, pos2: Vec2d, from: Vec2d, to: Vec2d) =
        ((pos1.y in from.y..to.y) ||
        (pos2.y in from.y..to.y) ||
        (from.y in pos1.y..pos2.y) ||
        (to.y in pos1.y..pos2.y)) && windowHeight > 1.0

    override fun onMouseAction(action: MouseAction, button: Int) {
        super.onMouseAction(action, button)

        if (action == MouseAction.CLICK) {
            when (button) {
                1 -> {
                    if (hovered) {
                        extended = !extended
                        modules.forEach {
                            it.extended = false
                        }
                    }
                }

                0 -> {
                    val time = System.currentTimeMillis()

                    if (hovered && extended && time - lastClickTime < 400) {
                        targetWindowHeight = modules.sumOf { it.getButtonHeight() } + ClickGui.panelRound + 1.0
                    }

                    if (draggingHovered) {
                        dragPos = gui.mouse.y - targetWindowHeight
                        isDraggingHeight = true
                        return
                    }

                    lastClickTime = time
                }
            }
        } else isDraggingHeight = false


        if (action == MouseAction.CLICK && !modulesHovered) return
        modules.filter { extended && panelFocused }.forEach { it.onMouseAction(action, button) }
    }

    override fun onKey(typedChar: Char, key: Int) = modules.filter { extended }.forEach { it.onKey(typedChar, key) }

    override fun equals(other: Any?): Boolean {
        return (other as? CategoryPanel)?.category == category
    }

    override fun hashCode(): Int {
        return category.hashCode()
    }
}