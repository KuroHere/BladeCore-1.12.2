package com.bladecore.client.command.commands

import com.bladecore.client.command.Command
import com.bladecore.client.utils.player.ChatUtils.sendMessage


object VClipCommand: Command(
    "vclip",
    "Changes your position",
    ".vclip DISTANCE"
) {
    override fun onExecute(args: ArrayList<String>) {
        if(args.size != 1){
            onBadUsage()
            return
        }

        val dist: Double

        try {
            dist = args[0].toDouble()
        } catch (e: Exception) {
            sendMessage("${args[0]} is not a valid number.")
            return
        }

        mc.player.setPosition(mc.player.posX, mc.player.posY + dist, mc.player.posZ)

    }
}