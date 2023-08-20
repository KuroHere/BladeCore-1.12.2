package com.bladecore.client.manager.managers

import com.bladecore.client.command.Command
import com.bladecore.client.command.commands.*

object CommandManager {
    fun getCommands(): ArrayList<Command>{
        return arrayListOf(
            BindCommand,
            FriendCommand,
            HClipCommand,
            HelpCommand,
            ToggleCommand,
            VClipCommand
        )
    }

    fun getCommandByNameOrNull(name: String): Command?{
        var command: Command? = null
        for(cmd in getCommands()){
            if(cmd.name.equals(name, true)){
                command = cmd
                break
            }
        }
        return command
    }
}