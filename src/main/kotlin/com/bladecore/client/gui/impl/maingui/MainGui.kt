package com.bladecore.client.gui.impl.maingui

import com.bladecore.client.gui.impl.maingui.elements.GeneralButton
import com.bladecore.client.module.modules.client.MenuShader
import com.bladecore.client.utils.render.ColorUtils
import com.bladecore.client.utils.render.RenderUtils2D
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11


class MainGui: GuiScreen() {
    private val buttons = ArrayList<GeneralButton>()


    override fun initGui() {
        updateButtons()
        MenuShader.initTime = System.currentTimeMillis()
    }

    private fun updateButtons(){
        buttons.clear()
        val width = 180
        val height = 25
        val sr = ScaledResolution(mc)

        buttons.add(GeneralButton(
            sr.scaledWidth / 2,
            sr.scaledHeight / 2 - 45,
            width,
            height,
            "SinglePlayer",
            EnumAction.SinglePlayer
        ))

        buttons.add(GeneralButton(
            sr.scaledWidth / 2,
            sr.scaledHeight / 2 - 15,
            width,
            height,
            "MultiPlayer",
            EnumAction.MultiPlayer
        ))

        buttons.add(GeneralButton(
            sr.scaledWidth / 2,
            sr.scaledHeight / 2 + 15,
            width,
            height,
            "AltManager",
            EnumAction.AltManager
        ))

        buttons.add(GeneralButton(
            sr.scaledWidth / 2,
            sr.scaledHeight / 2 + 45,
            width,
            height,
            "Settings",
            EnumAction.Settings
        ))

        buttons.add(GeneralButton(
            sr.scaledWidth / 2,
            sr.scaledHeight / 2 + 75,
            width,
            height,
            "Exit",
            EnumAction.Exit
        ))
    }



    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val sr = ScaledResolution(mc)
        //Gui.drawRect(0, 0, sr.scaledWidth, sr.scaledHeight, Color(20, 20, 20).hashCode())
        drawDefaultBackground()
        for(button in buttons){
            button.onDraw(mouseX, mouseY, partialTicks, this)
        }

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils2D.drawImage(
            ColorUtils.logo,
            sr.scaledWidth / 2 - 90,
            sr.scaledHeight / 2 - 100,
            180,
             25
        )

        GL11.glPopMatrix()
        GL11.glDisable(GL11.GL_BLEND)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        for(button in buttons){
            button.onMouseClick(mouseX, mouseY, mouseButton, this)
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        for(button in buttons){
            button.onMouseRelease(mouseX, mouseY, state, this)
        }
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    enum class EnumAction{
        SinglePlayer,
        MultiPlayer,
        AltManager,
        Settings,
        Exit
    }
}