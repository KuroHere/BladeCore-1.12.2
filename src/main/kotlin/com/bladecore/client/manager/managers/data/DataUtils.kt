package com.bladecore.client.manager.managers.data

import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.module.Module

object DataUtils {
    fun getModuleByName(name: String): Module {
        return ModuleManager.getModules().first { it.name.equals(name, true) }
    }
}