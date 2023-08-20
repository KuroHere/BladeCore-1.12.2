package com.bladecore.client.manager

import com.bladecore.client.event.EventBus
import com.bladecore.client.manager.managers.*
import com.bladecore.client.manager.managers.data.DataManager

object ManagerLoader {
    fun load(){
        EventBus.subscribe(DataManager)

        EventBus.subscribe(CommandManager)
        EventBus.subscribe(FriendManager)
        EventBus.subscribe(ModuleManager)
        EventBus.subscribe(PacketManager)
        EventBus.subscribe(TimerManager)
        EventBus.subscribe(HotbarManager)
    }
}