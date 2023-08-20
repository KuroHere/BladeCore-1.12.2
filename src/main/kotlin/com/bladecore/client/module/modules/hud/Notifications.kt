package com.bladecore.client.module.modules.hud

import com.bladecore.client.event.events.BladeCoreEvent
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.DraggableHudModule
import com.bladecore.client.module.modules.client.HUD
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.NotificationInfo
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.MathUtils.lerp
import com.bladecore.client.utils.math.NewEaseType
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.player.ChatUtils
import com.bladecore.client.utils.render.Screen
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.FontUtils.getStringWidth
import com.bladecore.client.utils.render.font.Fonts
import com.bladecore.client.utils.render.shader.RectBuilder
import java.awt.Color
import kotlin.math.max

object Notifications : DraggableHudModule(
    "Notifications",
    "Shows notifications in hud/chat",
) {
    private val mode by setting("Mode", Mode.Classic)
    private val widthSetting by setting("Width", 110.0, 80.0, 150.0, 1.0)
    private val heightSetting by setting("Height", 20.0, 14.0, 30.0, 1.0)

    val notificationList = ArrayList<Notification>()

    private enum class Mode {
        Classic,
        Transparent,
        Chat,
    }

    private const val keepTime = 2000L

    private const val showTime = 300L
    private const val hideTime = 500L

    init {
        safeListener<BladeCoreEvent.NotificationEvent> { event ->
            if (mode == Mode.Chat) {
                ChatUtils.sendMessage("${event.notification.text} ${event.notification.description}")
                return@safeListener
            }

            Notification(event.notification, System.currentTimeMillis()).spawn()
        }
    }

    override fun onRender() {
        notificationList.removeIf { it.shouldRemove() }

        var y = pos.y
        notificationList.forEach {
            it.draw(Vec2d(pos.x, y))
            y -= it.getProgress() * (getHeight() + 5.0)
        }
    }

    class Notification(val info: NotificationInfo, private val spawnTime: Long) {
        private val timeExisted get() = System.currentTimeMillis() - spawnTime

        fun spawn() {
            notificationList.add(this)
        }

        fun draw(position: Vec2d) {

            val xShift = max(0.0, getProgress() * 10.0 - 10.0) * 10.0
            val pos1 = Vec2d(lerp(Screen.scaledWidth, position.x, getProgress()) - xShift, position.y)
            val pos2 = pos1.plus(getWidth(), getHeight())

            when(mode) {
                Mode.Classic -> {
                    RectBuilder(pos1, pos2).color(Color(25, 25, 25)).radius(3.0).draw()

                    val y = lerp(pos1.y, pos2.y, 0.5)
                    val space = 4.0

                    Fonts.DEFAULT.drawString(info.text, Vec2d(pos1.x + space, y), color = info.mainColor)
                    Fonts.DEFAULT.drawString(info.description, Vec2d(pos2.x - space - Fonts.DEFAULT.getStringWidth(info.description), y), color = info.descriptionColor)
                }
                Mode.Transparent -> {
                    RectBuilder(pos1, pos2).color(Color(0, 0,  0, 120)).radius(8.0).draw()

                    val y = lerp(pos1.y, pos2.y, 0.5)
                    val space = 4.0

                    Fonts.DEFAULT.drawString(info.text, Vec2d(pos1.x + space, y), color = info.mainColor)
                    Fonts.DEFAULT.drawString(info.description, Vec2d(pos2.x - space - Fonts.DEFAULT.getStringWidth(info.description), y), color = info.descriptionColor)
                }
                Mode.Chat -> {}
            }
        }

        fun getProgress(): Double {
            var p = 0.0

            if (timeExisted <= showTime) p = (timeExisted.toDouble() / showTime.toDouble())
            if (timeExisted > showTime) p = 1.0
            if (timeExisted > showTime + keepTime) p = 1.0 - ((timeExisted - showTime - keepTime).toDouble() / hideTime.toDouble())

            return NewEaseType.OutBack.getValue(clamp(p, 0.0, 1.0))
        }

        fun shouldRemove() =
            timeExisted > showTime + keepTime + hideTime + 100L
    }


    override fun getWidth() = widthSetting
    override fun getHeight() = heightSetting
}