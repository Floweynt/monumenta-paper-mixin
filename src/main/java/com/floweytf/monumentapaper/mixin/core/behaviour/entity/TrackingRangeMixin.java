package com.floweytf.monumentapaper.mixin.core.behaviour.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import org.spigotmc.TrackingRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Flowey
 * @mm-patch 0033-Monumenta-Mob-behavior-changes.patch
 *
 * Increase viewing dist of giants
 */
@Mixin(TrackingRange.class)
public class TrackingRangeMixin {
    @ModifyReturnValue(
        method = "getEntityTrackingRange",
        at = @At("TAIL")
    )
    private static int func(int original, Entity entity) {
        if (entity instanceof net.minecraft.world.entity.monster.Giant) {
            return 96;
        }
        return original;
    }
}
