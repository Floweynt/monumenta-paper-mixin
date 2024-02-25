package com.floweytf.monumentapaper.mixin.core.behaviour.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Flowey
 * @mm-patch 0006-Monumenta-Block-behavior-changes.patch
 * <p>
 * Disable coral death
 */
@Mixin(CoralBlock.class)
public class CoralBlockMixin {
    /**
     * @author Flowey
     * @reason Disable coral death
     */
    @Overwrite
    protected static boolean scanForWater(BlockGetter world, BlockPos pos) {
        return true;
    }
}
