package com.bladecore.client.event.events

import com.bladecore.client.event.Event
import com.bladecore.client.module.Module
import com.bladecore.client.utils.NotificationInfo

abstract class BladeCoreEvent : Event {
    class NotificationEvent(val notification: NotificationInfo) : BladeCoreEvent()
    class ModuleToggleEvent(val module: Module) : BladeCoreEvent()

    abstract class LoadEvent : BladeCoreEvent() {
        class PreInit : LoadEvent()
        class Init : LoadEvent()
        class PostInit : LoadEvent()
    }
}