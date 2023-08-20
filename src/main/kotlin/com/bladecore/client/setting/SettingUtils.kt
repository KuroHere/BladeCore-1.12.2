package com.bladecore.client.setting

import com.bladecore.client.module.Module

inline fun <reified T : Setting<*>> Module.getSetting(name: String): T? {
    return settings.filterIsInstance<T>().firstOrNull { it.name.equals(name, ignoreCase = true) }
}

inline fun <reified T : Setting<*>> Module.getSettingNotNull(name: String): T {
    return settings.filterIsInstance<T>().firstOrNull { it.name.equals(name, ignoreCase = true) }!!
}