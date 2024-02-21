package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * @author Flowey
 * @mm-patch 0016-Monumenta-Reset-last-player-hurt-time-on-taking-any-.patch
 *
 * Honestly I have *no* clue what this patch does, but I can't be bothered to figure it out...
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    @Nullable
    public Player lastHurtByPlayer;

    @Shadow
    public int lastHurtByPlayerTime;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(
        method = "hurt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/damagesource/DamageSource;getEntity()Lnet/minecraft/world/entity/Entity;"
        )
    )
    private void resetHurtTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (lastHurtByPlayer != null) {
            lastHurtByPlayerTime = 100;
        }
    }
}