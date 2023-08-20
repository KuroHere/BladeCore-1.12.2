package com.bladecore.client.event.listener

import com.bladecore.client.event.ClientEvent
import com.bladecore.client.event.ListenerManager
import com.bladecore.client.event.SafeClientEvent

inline fun <reified T : Any> Any.safeListener(priority: Int = DEFAULT_PRIORITY, noinline function: SafeClientEvent.(T) -> Unit) {
    this.safeListener(priority, T::class.java, function)
}

fun <T : Any> Any.safeListener(priority: Int = DEFAULT_PRIORITY, clazz: Class<T>, function: SafeClientEvent.(T) -> Unit) {
    ListenerManager.register(this, Listener(this, clazz, priority) { runSafe { function(it) } })
}

fun ClientEvent.toSafe() =
    if (world != null && player != null && playerController != null && connection != null) SafeClientEvent(world, player, playerController, connection)
    else null