package com.bladecore.mixin.render;

import com.bladecore.client.module.modules.render.GlintColor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {
    @Shadow protected abstract void renderModel(IBakedModel model, int color);

    @Redirect(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"))
    private void renderEffect(RenderItem instance, IBakedModel model, int color) {
        int c = -8372020;

        if (GlintColor.INSTANCE.isEnabled()) c = GlintColor.getColor().getRGB();

        this.renderModel(model, c);
    }
}
