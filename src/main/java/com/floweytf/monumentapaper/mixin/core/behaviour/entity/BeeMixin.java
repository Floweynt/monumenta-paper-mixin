package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Flowey
 * @mm-patch 0024-Monumenta-Remove-bee-death-neutral-after-sting.patch
 *
 * Bees should not loose agro on death
 */
@Mixin(Bee.class)
public class BeeMixin {
    @Redirect(method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;setHasStung(Z)V"
        )
    )
    private void cancelSetHasStung(Bee instance, boolean hasStung) {
    }

    @Redirect(method = "doHurtTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Bee;stopBeingAngry()V"
        )
    )
    private void cancelStopBeingAngry(Bee instance) {
    }
}
