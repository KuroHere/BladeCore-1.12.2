package com.bladecore.client.command

import com.bladecore.client.utils.player.ChatUtils
import net.minecraft.client.Minecraft

open class Command(
    val name: String,
    val description: String,
    val usage: String
) {
    protected val mc: Minecraft = Minecraft.getMinecraft()
    open fun onExecute(args: ArrayList<String>){}
    open fun onBadUsage(){ ChatUtils.sendMessage("Bad $name command usage, try: $usage") }
}