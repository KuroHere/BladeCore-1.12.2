package com.bladecore.mixin.render;

import com.bladecore.client.module.modules.client.MenuShader;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {
    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    void drawBackground(CallbackInfo ci) {
        if (!MenuShader.INSTANCE.isEnabled()) return;

        ci.cancel();
        MenuShader.draw();
    }
}
