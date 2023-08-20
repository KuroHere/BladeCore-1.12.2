package com.bladecore.client.manager.managers.data

import com.bladecore.BladeCore
import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.manager.Manager
import com.bladecore.client.manager.managers.data.controllers.ModuleDataController
import com.bladecore.client.manager.managers.data.controllers.SettingsDataController
import java.io.File

object DataManager : Manager("DataManager") {
    private val controllers = ArrayList<DataController>()

    init {
        listener<ConnectionEvent.Connect> {
            saveConfig()
        }

        listener<ConnectionEvent.Disconnect> {
            saveConfig()
        }

        controllers.add(ModuleDataController)
        controllers.add(SettingsDataController)
    }

    fun onClientLoad(){
        val dir = File(BladeCore.DIR)
        if (!dir.exists()) dir.mkdir()
        loadConfig()
    }

    fun saveConfig() {
        for(c in controllers){
            c.onWrite()
        }
    }

    private fun loadConfig() {
        for(c in controllers){
            c.onLoad()
        }
    }
}