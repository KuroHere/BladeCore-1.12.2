package com.bladecore.mixin.render;

import com.bladecore.client.event.EventBus;
import com.bladecore.client.event.events.Render2DEvent;
import com.bladecore.client.module.modules.client.PerformancePlus;
import com.bladecore.client.module.modules.player.NoEntityTrace;
import com.bladecore.client.module.modules.render.*;
import com.bladecore.client.utils.render.ProjectionUtils;
import com.bladecore.client.utils.render.Screen;
import com.bladecore.mixin.accessor.render.AccessorEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("")
@Mixin(value = EntityRenderer.class, priority = Integer.MAX_VALUE)
public class MixinEntityRenderer {
    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V", shift = At.Shift.AFTER))
    public void updateCameraAndRender(float partialTicks, long nanoTime, CallbackInfo ci) {
        ProjectionUtils.INSTANCE.updateMatrix();
        Screen.INSTANCE.pushRescale();
        EventBus.INSTANCE.post(new Render2DEvent());
        Screen.INSTANCE.popRescale();
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void onHurtCameraEffect(float partialTicks, CallbackInfo ci) {
        if(NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getHurt()) ci.cancel();
    }

    @Inject(method = "applyBobbing", at = @At("HEAD"), cancellable = true)
    private void onCameraBob(float partialTicks, CallbackInfo ci) {
        if(NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getBobbing()) ci.cancel();
    }

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void onNametagRender(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        if (Nametags.INSTANCE.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderWorldPass", at = @At("RETURN"))
    private void renderWorldPassPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        HandShader.drawArm(partialTicks, pass);
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(final EntityPlayerSP entityPlayerSP) {
        return NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getPortal() ? -3.4028235E38f : entityPlayerSP.prevTimeInPortal;
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    public RayTraceResult orientCamera$ModifyVariable$0$STORE$0(RayTraceResult value) {
        if (ThirdPersonCamera.INSTANCE.isEnabled()) {
            return null;
        } else {
            return value;
        }
    }

    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    public double orientCamera$ModifyVariable$3$STORE$0(double value) {
        if (ThirdPersonCamera.INSTANCE.isEnabled()) {
            return ThirdPersonCamera.INSTANCE.getDistance();
        } else {
            return value;
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    void renderRainSnowInject(float partialTicks, CallbackInfo ci) {
        if (!PerformancePlus.INSTANCE.isEnabled()) return;
        if (!PerformancePlus.INSTANCE.getHideWeatherEffects()) return;
        ci.cancel();
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    void addRainParticlesInject(CallbackInfo ci) {
        if (!PerformancePlus.INSTANCE.isEnabled()) return;
        if (!PerformancePlus.INSTANCE.getHideWeatherEffects()) return;
        ci.cancel();
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    void onLightMapUpdate(CallbackInfo ci) {
        EntityRenderer instance = (EntityRenderer) (Object) this;
        if (!((AccessorEntityRenderer)instance).getLightmapUpdateNeeded()) return;
        boolean flag = PerformancePlus.shouldUpdateLightMap();
        if (!flag) ci.cancel();
    }

    @Inject(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPositionEyes(F)Lnet/minecraft/util/math/Vec3d;", shift = At.Shift.BEFORE), cancellable = true)
    public void getEntitiesInAABBexcluding(float partialTicks, CallbackInfo ci) {
        if (!NoEntityTrace.isActive()) return;

        ci.cancel();
        Minecraft.getMinecraft().profiler.endSection();
    }
}
