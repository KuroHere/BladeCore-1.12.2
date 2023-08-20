package com.bladecore.client

import com.bladecore.client.command.ChatRedirector
import com.bladecore.client.event.EventBus
import com.bladecore.client.event.EventProcessor
import com.bladecore.client.event.events.BladeCoreEvent
import com.bladecore.client.gui.GuiUtils
import com.bladecore.client.manager.ManagerLoader
import com.bladecore.client.manager.managers.FriendManager
import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.manager.managers.data.DataManager
import com.bladecore.client.utils.render.texture.TextureUtils
import com.bladecore.client.utils.render.font.FontRenderer
import net.minecraftforge.common.MinecraftForge

object Loader {
    fun onPreLoad() {
        ModuleManager.load() // load modules

        ManagerLoader.load() // subscribe managers

        DataManager.onClientLoad() // load config
        FriendManager.loadConfig() // load friend list

        EventBus.post(BladeCoreEvent.LoadEvent.PreInit())
    }

    fun onLoad() {
        MinecraftForge.EVENT_BUS.register(EventProcessor) // handle forge events
        EventBus.subscribe(ChatRedirector) // load commands

        EventBus.post(BladeCoreEvent.LoadEvent.Init())
    }

    fun onPostLoad() {
        // load fonts
        FontRenderer.reloadFonts()

        GuiUtils.bootstrap() // load guis

        EventBus.post(BladeCoreEvent.LoadEvent.PostInit())
    }
}