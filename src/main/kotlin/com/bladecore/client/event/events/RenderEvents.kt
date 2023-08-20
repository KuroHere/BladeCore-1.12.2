package com.bladecore.client.event.events

import com.bladecore.client.event.Cancellable
import com.bladecore.client.event.Event
import com.bladecore.client.event.ICancellable
import net.minecraft.entity.Entity

abstract class RenderEntityEvent(val entity: Entity) : Event, ICancellable by Cancellable() {
    class Pre(entityIn: Entity) : RenderEntityEvent(entityIn)
    class Peri(entityIn: Entity) : RenderEntityEvent(entityIn)
    class Post(entityIn: Entity) : RenderEntityEvent(entityIn)

    class ModelPre(entity: Entity) : RenderEntityEvent(entity)
    class ModelPost(entity: Entity) : RenderEntityEvent(entity)

    companion object {
        @JvmStatic
        var renderingEntities = false
    }
}

class Render2DEvent : Event

class Render3DEvent : Event

class ResolutionUpdateEvent(val width: Int, val height: Int) : Event