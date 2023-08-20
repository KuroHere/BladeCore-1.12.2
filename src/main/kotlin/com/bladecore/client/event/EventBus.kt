package com.bladecore.client.event

import com.bladecore.client.event.eventbus.AbstractAsyncEventBus
import com.bladecore.client.event.listener.AsyncListener
import com.bladecore.client.event.listener.Listener
import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

object EventBus : AbstractAsyncEventBus() {
    override val subscribedListeners = ConcurrentHashMap<Class<*>, MutableSet<Listener<*>>>()
    override val subscribedListenersAsync = ConcurrentHashMap<Class<*>, MutableSet<AsyncListener<*>>>()

    override fun post(event: Any) {
        subscribedListeners[event.javaClass]?.forEach {
            @Suppress("UNCHECKED_CAST") // IDE meme
            (it as Listener<Any>).function.invoke(event)
        }

        /*val listeners = subscribedListenersAsync[event.javaClass] ?: return

        if (listeners.isNotEmpty()) {
            runBlocking {
                listeners.forEach {
                    launch(Dispatchers.Default) {
                        @Suppress("UNCHECKED_CAST")
                        (it as AsyncListener<Any>).function.invoke(event)
                    }
                }
            }
        }*/
    }

    override fun newSet() = ConcurrentSkipListSet<Listener<*>>(Comparator.reverseOrder())

    override fun newSetAsync() = ConcurrentSet<AsyncListener<*>>()
}