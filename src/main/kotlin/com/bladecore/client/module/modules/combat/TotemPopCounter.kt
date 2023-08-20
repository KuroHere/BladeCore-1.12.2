package com.bladecore.client.module.modules.combat

import com.bladecore.client.event.events.ConnectionEvent
import com.bladecore.client.event.events.TotemPopEvent
import com.bladecore.client.event.listener.listener
import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.managers.FriendManager.isFriend
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import com.bladecore.client.utils.player.ChatUtils.sendMessage
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.gameevent.TickEvent

object TotemPopCounter: Module(
    "TotemPopCounter",
    "Counts popped totems",
    Category.COMBAT
) {
    private val countFriends by setting("Count Friends", true)
    private val countSelf by setting("Count Self", true)

    private val popped = HashMap<EntityPlayer, Int>()

    init {
        safeListener<TotemPopEvent> { event ->
            val isFriend = event.player.isFriend()
            val isSelf = event.player == player

            if (!countFriends && isFriend) return@safeListener
            if (!countSelf && isSelf) return@safeListener

            val count = (popped[event.player] ?: 0) + 1
            popped[event.player] = count

            sendMessage("${event.player.name} popped ${formatCount(count)}!")
        }

        safeListener<TickEvent.ClientTickEvent> { event ->
            if (event.phase != TickEvent.Phase.END) return@safeListener

            if (!player.isEntityAlive) {
                popped.clear()
                return@safeListener
            }

            popped.entries.removeIf { (diedPlayer, popped) ->
                if (diedPlayer == player || diedPlayer.isEntityAlive) return@removeIf false

                sendMessage("${diedPlayer.name} died after popping ${formatCount(popped)}!")
                return@removeIf true
            }
        }

        listener<ConnectionEvent.Connect> {
            popped.clear()
        }

        listener<ConnectionEvent.Disconnect> {
            popped.clear()
        }
    }

    override fun onEnable() {
        popped.clear()
    }

    override fun onDisable() {
        popped.clear()
    }

    private fun formatCount(count: Int): String {
        return "$count " + if (count > 1) "totems" else "totem"
    }
}