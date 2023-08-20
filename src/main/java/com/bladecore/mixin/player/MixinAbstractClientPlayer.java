package com.bladecore.mixin.player;

import com.bladecore.client.module.modules.render.CustomFov;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {
    private static final ResourceLocation capeLocation = new ResourceLocation("bladecore", "cape.png");

    @Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
    private void onFovChange(CallbackInfoReturnable<Float> cir){
        if(CustomFov.INSTANCE.isEnabled()){
            float mod = 1.0f;
            if(CustomFov.INSTANCE.getAllowSprint() && CustomFov.INSTANCE.getStatic() && Minecraft.getMinecraft().player.isSprinting()) mod *= 1.15f;

            cir.setReturnValue(mod);
        }
    }

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCapeInject(CallbackInfoReturnable<ResourceLocation> cir) {
        AbstractClientPlayer instance = (AbstractClientPlayer) (Object) this;
        if (instance != Minecraft.getMinecraft().player) return;
        cir.setReturnValue(capeLocation);
    }
}