package com.floweytf.monumentapaper.mixin.core.behaviour.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 *
 * Disable concrete hardening when exposed to water in game
 */
@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin {
    @Inject(
        method = "shouldSolidify",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void monumenta$disableConcreteSolidify(BlockGetter world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
