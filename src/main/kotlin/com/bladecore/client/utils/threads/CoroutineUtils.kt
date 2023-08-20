package com.bladecore.client.utils.threads

import com.bladecore.client.event.ClientEvent
import com.bladecore.client.event.SafeClientEvent
import com.bladecore.client.event.listener.runSafe
import com.bladecore.client.event.listener.toSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultScope = CoroutineScope(Dispatchers.Default)

fun CoroutineScope.safe(block: SafeClientEvent.() -> Unit) =
    this.launch { runSafe { block() } }

fun runAsync(block: suspend () -> Unit) =
    runAsync(defaultScope, block)

fun runAsync(scope: CoroutineScope, block: suspend () -> Unit) =
    scope.launch {
        try { block() } catch (_: Exception) {}
    }
