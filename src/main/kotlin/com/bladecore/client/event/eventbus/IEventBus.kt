package com.bladecore.client.event.eventbus

import com.bladecore.client.event.listener.Listener

interface IEventBus {
    val subscribedListeners: MutableMap<Class<*>, MutableSet<Listener<*>>>

    fun subscribe(objs: Any)

    fun unsubscribe(objs: Any)

    fun post(event: Any)

    fun newSet(): MutableSet<Listener<*>>
}