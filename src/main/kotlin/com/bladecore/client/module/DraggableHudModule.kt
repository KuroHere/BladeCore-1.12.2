package com.bladecore.client.module

import com.bladecore.client.gui.GuiUtils
import com.bladecore.client.gui.api.other.MouseAction
import com.bladecore.client.module.modules.client.HudEditor
import com.bladecore.client.setting.getSetting
import com.bladecore.client.setting.getSettingNotNull
import com.bladecore.client.setting.setting
import com.bladecore.client.setting.type.DoubleSetting
import com.bladecore.client.setting.type.EnumSetting
import com.bladecore.client.setting.type.UnitSetting
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.ColorUtils.setAlphaD
import com.bladecore.client.utils.render.DockingH
import com.bladecore.client.utils.render.DockingV
import com.bladecore.client.utils.render.HoverUtils.isHovered
import com.bladecore.client.utils.render.RenderUtils2D
import com.bladecore.client.utils.render.Screen
import com.bladecore.client.utils.render.shader.RectBuilder
import java.awt.Color

abstract class DraggableHudModule(
    name: String,
    description: String,
    alwaysListenable: Boolean = false,
) : HudModule(name, description, alwaysListenable) {
    var isDragging = false
    private var dragX = 0.0
    private var dragY = 0.0

    init {
        settings.add(UnitSetting("Reset Position", {
            getSetting<EnumSetting<*>>("DockingH")?.setByName("Left")
            getSetting<EnumSetting<*>>("DockingV")?.setByName("Top")

            x = getDefaultPos().x
            y = getDefaultPos().y
        }))

        settings.add(DoubleSetting("X", 5.0, 0.0, 0.0, 1.0, { false }))
        settings.add(DoubleSetting("Y", 5.0, 0.0, 0.0, 1.0, { false }))


        val h = setting("DockingH", DockingH.LEFT)
        val v = setting("DockingV", DockingV.TOP)
        h.listeners.add { updatePosByDocking() }
        v.listeners.add { updatePosByDocking() }
    }

    private var x by getSettingNotNull<DoubleSetting>("X")
    private var y by getSettingNotNull<DoubleSetting>("Y")

    var dockingH by getSettingNotNull<EnumSetting<DockingH>>("DockingH")
    var dockingV by getSettingNotNull<EnumSetting<DockingV>>("DockingV")

    fun onRenderPre() {
        if (!HudEditor.isEnabled() || !isDragging || !isEnabled()) return
        val cornerPos = Vec2d(dockingH.modifier * Screen.width / 2.0, dockingV.modifier * Screen.height / 2.0)
        pos = GuiUtils.hudEditorGui!!.mouse.minus(dragX, dragY).minus(cornerPos)
    }

    fun onRenderPost() {
        if (!HudEditor.isEnabled() || !isEnabled()) return
        /*RectBuilder(pos, pos.plus(getWidth(), getHeight())).apply {
            color(Color.WHITE.setAlphaD(0.0))
            outlineColor(Color.WHITE.setAlphaD(0.7))
            width(1.0)
            draw()
        }*/
    }

    fun handleMouseAction(mouse: Vec2d, action: MouseAction, button: Int) {
        when (action) {
            MouseAction.CLICK -> {
                if (button != 0) return
                if (isHovered(mouse, pos, pos.plus(getWidth(), getHeight()))){
                    dragX = mouse.x - pos.x
                    dragY = mouse.y - pos.y
                    isDragging = true
                }
            }

            MouseAction.RELEASE -> {
                isDragging = false
            }
        }
    }

    protected var pos: Vec2d
        get() = getPosByDocking()
        set(value) { x = value.x; y = value.y }

    open fun getWidth(): Double { return 10.0 }
    open fun getHeight(): Double { return 10.0 }

    open fun getDefaultPos(): Vec2d {
        return Vec2d(5.0, 5.0)
    }

    private fun getPosByDocking(): Vec2d {
        val cornerPos = Vec2d(dockingH.modifier * Screen.width / 2.0, dockingV.modifier * Screen.height / 2.0)
        return Vec2d(x, y).plus(cornerPos)
    }

    private fun updatePosByDocking() {
        val cornerPos = Vec2d(dockingH.modifier * Screen.width / 2.0, dockingV.modifier * Screen.height / 2.0)
        x = (Screen.width / 2.0 - getWidth()) * dockingH.modifier - cornerPos.x
        y = (Screen.height / 2.0 - getHeight()) * dockingV.modifier - cornerPos.y
    }
}