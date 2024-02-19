package com.floweytf.monumentamixins.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;

// https://github.com/TeamMonumenta/monumenta-paperfork/blob/1.19/patches/server/0005-Monumenta-Removed-test-for-monsters-when-sleeping-in.patch
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "getBedResult", at = @At(target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z", value = "INVOKE"), require = 1, cancellable = true)
    private void getBedResult(BlockPos _0, Direction _1, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        cir.setReturnValue(Either.left(Player.BedSleepingProblem.NOT_SAFE));
    }
}
