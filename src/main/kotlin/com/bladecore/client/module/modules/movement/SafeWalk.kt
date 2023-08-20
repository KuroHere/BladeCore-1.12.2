package com.bladecore.client.module.modules.movement

import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.player.Scaffold
import com.bladecore.client.utils.Wrapper

object SafeWalk: Module(
    "SafeWalk",
    "Keeps you on the edge of the block",
    Category.MOVEMENT
) {
    @JvmStatic
    fun shouldSafewalk(entityID: Int) =
        (Wrapper.player?.let { !it.isSneaking && it.entityId == entityID } ?: false)
            && (isEnabled() || (Scaffold.isEnabled() && Scaffold.safeWalk))

    @JvmStatic
    fun setSneaking(state: Boolean) {
        Wrapper.player?.movementInput?.sneak = state
    }
}