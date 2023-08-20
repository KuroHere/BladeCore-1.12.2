package com.bladecore.client.manager.managers

import com.bladecore.client.event.listener.safeListener
import com.bladecore.client.manager.Manager
import com.bladecore.client.module.Module
import com.bladecore.client.module.modules.client.*
import com.bladecore.client.module.modules.combat.*
import com.bladecore.client.module.modules.hud.*
import com.bladecore.client.module.modules.hud.modulelist.ModuleList
import com.bladecore.client.module.modules.movement.*
import com.bladecore.client.module.modules.player.*
import com.bladecore.client.module.modules.render.*
import com.bladecore.client.module.modules.misc.*
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

object ModuleManager: Manager("ModuleManager") {
    fun getModules(): ArrayList<Module> {
        return arrayListOf(
            //client
            ClickGui,
            HUD,
            FontSettings,
            HudEditor,
            MenuShader,
            PerformancePlus,

            //combat
            AntiBot,
            OffhandManager,
            Burrow,
            Criticals,
            CrystalAura,
            HitboxDesync,
            HoleMiner,
            KillAura,
            TotemPopCounter,
            Velocity,

            //hud
            ModuleList,
            Armor,
            FPS,
            Notifications,
            PlayerSpeed,
            RenderTest,
            TargetHUD,
            Watermark,

            //misc
            AutoRespawn,
            ChestStealer,
            ExplosionSoundFix,
            MiddleClick,
            FakePlayer,
            DeathSounds,
            Surround,
            HoleFiller,

            //movement
            ElytraFlight,
            FastFall,
            Flight,
            GuiWalk,
            HighJump,
            Jesus,
            KeepSprint,
            LongJump,
            NoFall,
            NoJumpDelay,
            NoSlow,
            PearlClip,
            Phase,
            ReverseStep,
            SafeWalk,
            Speed,
            Spider,
            Sprint,
            Step,
            TargetStrafe,

            //player
            AntiHunger,
            AntiRotate,
            AntiServerSlot,
            AutoEat,
            Blink,
            FastUse,
            FreeCam,
            NoEntityTrace,
            PacketLogger,
            PacketMine,
            Reach,
            SwingLimiter,
            Scaffold,
            TickShift,
            Timer,

            //render
            Chams,
            BlockESP,
            ChinaHat,
            CrystalRenderer,
            CustomFov,
            ESP,
            FancyHandshake,
            FogColor,
            FullBright,
            GlintColor,
            HandShader,
            HealthParticles,
            HitParticles,
            HoleESP,
            ItemPhysics,
            JumpCircles,
            Nametags,
            NoRender,
            SelectionHighlight,
            SmoothCrouch,
            TargetESP,
            ThirdPersonCamera,
            Tracers,
            Trails,
            ViewModel,
            VisualRotations,
            WorldTime
        )
    }

    fun load() = getModules().forEach {
        it.onInit()
        if (it.enabledByDefault) it.setEnabled(true)
    }

    init {
        safeListener<InputEvent.KeyInputEvent> {
            getModules()
                .filter { it.key != Keyboard.KEY_NONE && it.key == Keyboard.getEventKey() }
                .forEach { it.toggle() }
        }
    }
}