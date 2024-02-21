package com.floweytf.monumentapaper.mixin.core.behaviour.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 *
 * Disable coral death
 */
@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {
    // Just short-circuit evaluate
    // NODE: consider using @Overwrite when we do "shortcircuit function injection"
    // Although this isn't "good practice" according to MixinDocs, we have a bit of a special case here
    @Inject(
        method = "scanForWater",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void disableCoralDeath(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }
}
