package com.bladecore.client.command.commands

import com.bladecore.client.command.Command
import com.bladecore.client.manager.managers.CommandManager
import com.bladecore.client.manager.managers.CommandManager.getCommandByNameOrNull
import com.bladecore.client.utils.player.ChatUtils.sendMessage

object HelpCommand: Command(
    "help",
    "Shows help",
    ".help / .help command"
) {
    override fun onExecute(args: ArrayList<String>) {
        if(args.size > 1) {
            onBadUsage()
            return
        }

        if(args.isEmpty()){
            for (cmd in CommandManager.getCommands()){
                sendMessage("${cmd.name.uppercase()} - ${cmd.description}")
            }
            return
        }
        if(args.size == 1){
            val cmd = getCommandByNameOrNull(args[0])

            if(cmd == null){
                sendMessage("${args[0]} is not a valid command.")
                return
            }

            sendMessage("${cmd.name.uppercase()}:  ${cmd.usage}")
        }
    }
}