package com.bladecore.mixin;

import com.bladecore.client.module.modules.client.MenuShader;
import com.bladecore.client.module.modules.client.PerformancePlus;
import com.bladecore.client.utils.Wrapper;
import com.bladecore.client.utils.threads.MainThreadExecutor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Timer;updateTimer()V", shift = At.Shift.BEFORE))
    public void runGameLoopStart(CallbackInfo ci) {
        MainThreadExecutor.begin();
    }

    @Inject(method = "checkGLError", at = @At("HEAD"), cancellable = true)
    public void onGLCheck(String message, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void limitFramerate(CallbackInfoReturnable<Integer> cir) {
        Minecraft mc = Wrapper.getMinecraft();

        if (mc.world == null && mc.currentScreen != null && MenuShader.INSTANCE.isEnabled()) {
            cir.setReturnValue(mc.gameSettings.limitFramerate);
        }
    }
}
