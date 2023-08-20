package com.bladecore.client.module

import com.bladecore.client.event.EventBus
import com.bladecore.client.event.events.BladeCoreEvent
import com.bladecore.client.setting.Setting
import com.bladecore.client.setting.getSetting
import com.bladecore.client.setting.type.BooleanSetting
import com.bladecore.client.utils.NotificationType
import com.bladecore.client.utils.NotificationUtils
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import java.awt.Color

abstract class Module(
    val name: String,
    val description: String,
    val category: Category,
    var key: Int = Keyboard.KEY_NONE, // TODO: make as setting
    val alwaysListenable: Boolean = false,
    val enabledByDefault: Boolean = false
) {
    private var isEnabled = false // TODO: make as setting
    val isVisible get() = getSetting<BooleanSetting>("Visible")?.value ?: true

    var settings = ArrayList<Setting<*>>()

    protected val mc: Minecraft = Minecraft.getMinecraft()

    init {
        settings.add(BooleanSetting("Visible", true, visibility = { this !is HudModule }))
    }

    protected open fun onEnable() {}

    protected open fun onDisable() {}

    protected open fun onClientLoad() {}

    open fun getHudInfo():String {
        return ""
    }

    fun onInit() {
        if (alwaysListenable) EventBus.subscribe(this)
        onClientLoad()
    }

    enum class Category(val displayName: String, val index: Int) {
        //general
        COMBAT("Combat", 1),
        MOVEMENT("Movement", 2),
        PLAYER("Player", 3),
        RENDER("Render", 4),
        MISC("Misc", 5),
        CLIENT("Client", 6),
        HUD("Hud", 7)
    }

    fun toggle() { setEnabled(!isEnabled) }

    fun isEnabled(): Boolean {
        return isEnabled
    }

    fun setEnabled(state: Boolean){
        if (state) enable() else disable()
    }

    private fun enable(){
        if(!isEnabled){
            isEnabled = true
            if (!alwaysListenable) EventBus.subscribe(this)

            EventBus.post(BladeCoreEvent.ModuleToggleEvent(this))
            NotificationUtils.notify(name, "Enabled", NotificationType.INFO, descriptionColor = Color.GREEN)

            onEnable()
        }
    }

    private fun disable(){
        if (isEnabled){
            isEnabled = false
            if (!alwaysListenable) EventBus.unsubscribe(this)
            if (this is DraggableHudModule) isDragging = false

            EventBus.post(BladeCoreEvent.ModuleToggleEvent(this))
            NotificationUtils.notify(name, "Disabled", NotificationType.INFO, descriptionColor = Color.RED)

            onDisable()
        }
    }
}