package com.bladecore.mixin.entity;

import com.bladecore.client.event.EventBus;
import com.bladecore.client.event.events.EntityDeathEvent;
import com.bladecore.client.event.events.JumpEvent;
import com.bladecore.client.event.events.JumpMotionEvent;
import com.bladecore.client.module.modules.render.ViewModel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.lang.Math.max;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Inject(method = "getArmSwingAnimationEnd", at = @At("HEAD"), cancellable = true)
    private void onGetArmSwingAnimationEnd(CallbackInfoReturnable<Integer> cir) {
        if (!ViewModel.INSTANCE.isEnabled()) return;
        cir.setReturnValue((int)(6 * max(ViewModel.getSwingSpeed(), 0.2f)));
    }

    @Inject(method = "getJumpUpwardsMotion", at = @At("HEAD"), cancellable = true)
    private void onJumpMotion(CallbackInfoReturnable<Float> cir) {
        if (!this.getClass().isInstance(Minecraft.getMinecraft().player)) return;

        JumpMotionEvent event = new JumpMotionEvent(0.42f);
        EventBus.INSTANCE.post(event);
        if (event.getCancelled()) cir.setReturnValue(0f); else cir.setReturnValue(event.getMotion());
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if (!this.getClass().isInstance(Minecraft.getMinecraft().player)) return;

        JumpEvent event = new JumpEvent();
        EventBus.INSTANCE.post(event);
        if (event.getCancelled()) ci.cancel();
    }

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onSetHealth(float healthTo, CallbackInfo ci) {
        EntityLivingBase instance = (EntityLivingBase) (Object) this;

        float healthFrom = instance.getHealth();

        if (healthFrom <= 0.0) return;
        if (healthTo > 0.0) return;

        EventBus.INSTANCE.post(new EntityDeathEvent(instance));
    }
}
