package com.bladecore.client.manager.managers

import com.bladecore.BladeCore
import com.bladecore.client.manager.Manager
import com.bladecore.client.manager.managers.FriendManager.isFriend
import net.minecraft.entity.player.EntityPlayer
import java.io.File

object FriendManager: Manager("FriendManager") {
    private val file = File(BladeCore.DIR + "/friends.txt")
    private val friends = ArrayList<String>()

    fun addFriend(name: String): Boolean{
        if (friends.all { !it.equals(name, true) }) {
            friends.add(name)
            saveConfig()
            return true
        }
        return false
    }

    fun removeFriend(name: String): Boolean{
        if(friends.contains(name)){
            friends.remove(name)
            saveConfig()
            return true
        }
        return false
    }

    fun getFriendList(): ArrayList<String> {
        return friends
    }

    fun EntityPlayer.isFriend(): Boolean {
        return friends.any { it.equals(this.displayNameString, true) }
    }

    fun loadConfig(){
        if (!file.exists()) return
        val lines = file.readLines() as ArrayList<String>
        friends.clear()
        for (l in lines) {
            if(l.length > 1) friends.add(l)
        }
    }

    private fun saveConfig(){
        if (!file.exists()) file.createNewFile()
        var text = ""
        for (f in friends) {
            text += f + System.lineSeparator()
        }
        file.writeText(text)
    }
}