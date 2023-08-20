package com.bladecore.client.utils.extension

import com.bladecore.client.event.listener.Nameable

val Enum<*>.settingName: String get() = (this as? Nameable)?.displayName ?: this.name