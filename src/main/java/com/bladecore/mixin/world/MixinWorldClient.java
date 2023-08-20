package com.bladecore.mixin.world;

import com.bladecore.client.module.modules.client.PerformancePlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldClient.class)
public class MixinWorldClient {
    @Inject(method = "updateBlocks", at = @At(value = "HEAD"), cancellable = true)
    void updateBlocksInject(CallbackInfo ci) {
        if (!PerformancePlus.INSTANCE.isEnabled()) return;
        ci.cancel();
    }

    @Inject(method = "doVoidFogParticles", at = @At("HEAD"), cancellable = true)
    void fogParticlesInject(int posX, int posY, int posZ, CallbackInfo ci) {
        if (!PerformancePlus.INSTANCE.isEnabled()) return;
        ci.cancel();
    }
}
