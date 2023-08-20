package com.bladecore.mixin.accessor.entity;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayer.class)
public interface AccessorEntityPlayer {

    @Accessor("speedInAir")
    float getSpeedInAir();

    @Accessor("speedInAir")
    void setSpeedInAir(float value);
}
