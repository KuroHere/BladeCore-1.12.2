package com.bladecore.client.utils

import com.bladecore.client.event.EventBus
import com.bladecore.client.event.events.BladeCoreEvent
import net.minecraft.util.ResourceLocation
import java.awt.Color

object NotificationUtils {
    fun notify(text: String, description: String, type: NotificationType, mainColor: Color = Color.WHITE, descriptionColor: Color = Color.WHITE) {
        val notification = NotificationInfo(text, description, type, mainColor, descriptionColor)
        val event = BladeCoreEvent.NotificationEvent(notification)

        EventBus.post(event)
    }

    val notification_success = ResourceLocation("bladecore", "icons/notifications/success.png")
    val notification_error = ResourceLocation("bladecore", "icons/notifications/error.png")
    val notification_info = ResourceLocation("bladecore", "icons/notifications/info.png")
}

class NotificationInfo(val text: String, val description: String, val type: NotificationType, val mainColor: Color = Color.WHITE, val descriptionColor: Color = Color.WHITE)

enum class NotificationType(val icon: ResourceLocation, val typeName: String){
    SUCCESS(NotificationUtils.notification_success, "Success"),
    ERROR(NotificationUtils.notification_error, "Error"),
    INFO(NotificationUtils.notification_info, "Info")
}