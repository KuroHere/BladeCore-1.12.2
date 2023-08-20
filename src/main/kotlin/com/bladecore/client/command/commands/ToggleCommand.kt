package com.bladecore.client.command.commands

import com.bladecore.client.command.Command
import com.bladecore.client.manager.managers.ModuleManager
import com.bladecore.client.utils.player.ChatUtils

object ToggleCommand : Command(
    "toggle",
    "Toggles module",
    ".toggle MODULE"
) {
    override fun onExecute(args: ArrayList<String>) {
        if(args.count() != 1) {
            onBadUsage()
            return
        }

        val module = ModuleManager.getModules().firstOrNull { it.name.equals(args[0], true) }

        if (module == null){
            ChatUtils.sendMessage("${args[0]} is not a valid module.")
            return
        }

        module.toggle()
    }
}