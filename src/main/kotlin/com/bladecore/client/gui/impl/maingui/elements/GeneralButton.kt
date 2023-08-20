package com.bladecore.client.gui.impl.maingui.elements

import com.bladecore.client.gui.impl.altmanager.AltManagerGui
import com.bladecore.client.gui.impl.maingui.MainGui
import com.bladecore.client.gui.impl.maingui.MainGuiElement
import com.bladecore.client.utils.math.EaseUtils
import com.bladecore.client.utils.math.MathUtils.clamp
import com.bladecore.client.utils.math.Vec2d
import com.bladecore.client.utils.render.ColorUtils
import com.bladecore.client.utils.render.RenderUtils2D
import com.bladecore.client.utils.render.font.FontUtils.drawString
import com.bladecore.client.utils.render.font.Fonts
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiOptions
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiWorldSelection
import org.lwjgl.opengl.GL11.*
import java.awt.Color

class GeneralButton(xCenter: Int, yCenter: Int, width: Int, height: Int, val text: String, val action: MainGui.EnumAction) : MainGuiElement(xCenter, yCenter, width, height){
    private var isClicking = false

    override fun onRender(mouseX: Int, mouseY: Int, partialTicks: Float, mainGui: GuiScreen) {
        update(partialTicks)

        val fr = Fonts.DEFAULT

        //background
        val leftTop = Vec2d(getLeftTop().x.toDouble() - (getHoverProgress() * 2), getLeftTop().y.toDouble())
        val rightBottom = Vec2d(getRightBottom().x.toDouble() + (getHoverProgress() * 2), getRightBottom().y.toDouble())

        val color = if (!isHovered) 35 else {
            if (!isClicking) 45 else 55
        }

        RenderUtils2D.drawRect(
            leftTop,
            rightBottom,
            Color(color, color, color, 160)
        )

        fr.drawString(
            text,
            Vec2d(xCentered - (width / 2) + 10.0, yCentered.toDouble()),
            scale = 1.5
        )

        if (getHoverProgress() > 0.05) {
            drawIcon(
                xCentered + (width / 2) - (height / 2),
                yCentered + ((height / 2) * (1 - getHoverProgress()) * 0.9).toInt(),
                10 + (getHoverProgress() * 5).toInt(),
                10 + (getHoverProgress() * 5).toInt(),
                getHoverProgress().toFloat(),
            )
        }
    }

    private fun drawIcon(centerX: Int, centerY: Int, width: Int, height: Int, alphaIn: Float){
        val alpha = clamp(alphaIn, 0.0f, 1.0f)

        val targetImage = when(action){
            MainGui.EnumAction.SinglePlayer -> {
                ColorUtils.icon_singleplayer
            }

            MainGui.EnumAction.MultiPlayer -> {
                ColorUtils.icon_multiplayer
            }

            MainGui.EnumAction.AltManager -> {
                ColorUtils.icon_altmanager
            }

            MainGui.EnumAction.Settings -> {
                ColorUtils.icon_settings
            }

            MainGui.EnumAction.Exit -> {
                ColorUtils.icon_shutdown
            }
        }

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glPushMatrix()
        glColor4f(1f, 1f, 1f, alpha)
        RenderUtils2D.drawImage(targetImage, centerX - (width / 2), centerY - (height / 2), width, height)
        glPopMatrix()
        glDisable(GL_BLEND)
    }

    override fun onMouseClick(mouseX: Int, mouseY: Int, mouseButton: Int, mainGui: GuiScreen) {
        if(isHovered){
            isClicking = true
        }
    }

    override fun onMouseRelease(mouseX: Int, mouseY: Int, state: Int, mainGui: GuiScreen) {
        if(isClicking){
            isClicking = false
            runAction(mainGui)
        }
    }

    private fun runAction(mainGuiIn: GuiScreen){
        when(action){
            MainGui.EnumAction.SinglePlayer -> {
                mc.displayGuiScreen(GuiWorldSelection(mainGuiIn))
            }

            MainGui.EnumAction.MultiPlayer -> {
                mc.displayGuiScreen(GuiMultiplayer(mainGuiIn))
            }

            MainGui.EnumAction.AltManager -> {
                mc.displayGuiScreen(AltManagerGui())
            }

            MainGui.EnumAction.Settings -> {
                mc.displayGuiScreen(GuiOptions(mainGuiIn, mc.gameSettings))
            }

            MainGui.EnumAction.Exit -> {
                mc.shutdown()
            }
        }
    }

    private fun update(partialTicks: Float){
        if(isHovered) {
            hoverProgress += (0.4 * partialTicks)

        }else{
            hoverProgress -= (0.10 * partialTicks)
        }

        if(hoverProgress > 1.0) hoverProgress = 1.0
        if(hoverProgress < 0.0) hoverProgress = 0.0
    }

    private var hoverProgress = 0.0
    private fun getHoverProgress():Double{
        return clamp(EaseUtils.getEase(hoverProgress, EaseUtils.EaseType.InQuad), 0.0, 1.0)
    }

}