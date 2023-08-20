package com.bladecore.client.module.modules.render

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.module.Module
import com.bladecore.client.setting.setting
import net.minecraft.client.tutorial.TutorialSteps
import net.minecraft.init.MobEffects
import net.minecraftforge.client.event.RenderBlockOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object NoRender : Module(
    "NoRender",
    "Removes unuseful graphic",
    Category.RENDER
) {

    private val page by setting("Page", Page.General)

    //GENERAL
    val hurt by setting("NoHurtCam", false, visible = { page == Page.General })
    val bobbing by setting("NoBob", false, visible = { page == Page.General })

    //OVERLAYS
    private val fire by setting("Fire", false, visible = { page == Page.Overlays })
    private val water by setting("Water", false, visible = { page == Page.Overlays })
    private val blocks by setting("Blocks", false, visible = { page == Page.Overlays })
    private val pumpkin by setting("Pumpkin", false, visible = { page == Page.Overlays })
    val portal by setting("Portal", false, visible = { page == Page.Overlays })
    private val vignette by setting("Vignette", false, visible = { page == Page.Overlays })

    //POTIONS
    private val blindness by setting("Blindness", false, visible = { page == Page.Potions })
    private val nausea by setting("Nausea", false, visible = { page == Page.Potions })

    //HUD
    private val tutorialOverlay by setting("Tutorial Overlay", false, visible = { page == Page.HUD })
    private val potionIcons by setting("Potion Icons", false, visible = { page == Page.HUD })
    private val health by setting("Health", false, visible = { page == Page.HUD })
    private val food by setting("Food", false, visible = { page == Page.HUD })
    private val armor by setting("Armor", false, visible = { page == Page.HUD })
    private val experience by setting("Experience", false, visible = { page == Page.HUD })

    private enum class Page {
        General,
        Overlays,
        Potions,
        HUD
    }

    init {
        safeListener<RenderGameOverlayEvent.Pre> {
            it.isCanceled = when (it.type) {
                RenderGameOverlayEvent.ElementType.HELMET -> pumpkin
                RenderGameOverlayEvent.ElementType.POTION_ICONS -> potionIcons
                RenderGameOverlayEvent.ElementType.PORTAL -> portal
                RenderGameOverlayEvent.ElementType.VIGNETTE -> vignette
                RenderGameOverlayEvent.ElementType.HEALTH -> health
                RenderGameOverlayEvent.ElementType.FOOD -> food
                RenderGameOverlayEvent.ElementType.ARMOR -> armor
                RenderGameOverlayEvent.ElementType.EXPERIENCE -> experience

                else -> {
                    it.isCanceled
                }
            }
        }

        safeListener<RenderBlockOverlayEvent> {
            it.isCanceled = when (it.overlayType){
                RenderBlockOverlayEvent.OverlayType.FIRE -> fire
                RenderBlockOverlayEvent.OverlayType.WATER -> water
                RenderBlockOverlayEvent.OverlayType.BLOCK -> blocks
                else -> it.isCanceled
            }
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (blindness) player.removeActivePotionEffect(MobEffects.BLINDNESS)
            if (nausea) player.removeActivePotionEffect(MobEffects.NAUSEA)
            if (tutorialOverlay) mc.gameSettings.tutorialStep = TutorialSteps.NONE
        }
    }
}