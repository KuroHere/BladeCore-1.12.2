package com.bladecore.client.utils.player

import com.bladecore.client.Client
import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextComponentString

object ChatUtils {
    fun sendMessage(msg: String) {
        Minecraft.getMinecraft().player.sendMessage(TextComponentString(Client.chatName + " " + msg))
    }
}