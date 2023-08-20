package com.bladecore.client.gui.impl.altmanager

import com.bladecore.client.Client
import com.bladecore.client.gui.impl.maingui.MainGui
import com.bladecore.client.utils.player.SessionUtils
import com.mojang.authlib.Agent
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.Session
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.net.Proxy

class AltManagerGui : GuiScreen() {
    var inputField: GuiTextField? = null
    override fun initGui() {
        val i = height / 4 + 48
        buttonList.clear()
        inputField = GuiTextField(1, fontRenderer, width / 2 - 100, i + 30 - 12, 200, 20)
        try {
            inputField!!.text = mc.session.username
        }catch (e: Exception){
            inputField!!.text = ""
            e.printStackTrace()
        }
        buttonList.add(GuiButton(1, width / 2 - 100, i + 30 + 12, 200, 20, "Login"))
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 1) {
            changeName(inputField!!.text)
        }
    }

    override fun updateScreen() {
        inputField!!.updateCursorCounter()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> mc.displayGuiScreen(MainGui())
            else -> inputField!!.textboxKeyTyped(typedChar, keyCode)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        inputField!!.mouseClicked(mouseX, mouseY, mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GL11.glColor4f(1f, 1f, 1f, 1f)
        drawDefaultBackground()
        for (i in buttonList.indices) {
            (buttonList[i] as GuiButton).drawButton(mc, mouseX, mouseY, partialTicks)
        }
        inputField!!.drawTextBox()
        mc.fontRenderer.drawStringWithShadow(
            "Your Username: " + mc.session.username,
            (width / 2 - 100).toFloat(),
            (height / 4 + 48 + 110).toFloat(),
            Color.yellow.rgb
        )
    }

    companion object {
        fun changeName(name: String?) {
            val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "")
            val auth = service.createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
            auth.logOut()
            val session = Session(name!!, name, "0", "legacy")
            try {
                SessionUtils.setSession(session)
                Display.setTitle(Client.displayName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}