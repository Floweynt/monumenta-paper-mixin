package com.floweytf.monumentapaper.mixin.core.bugfix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.checkerframework.checker.units.qual.K;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author Flowey
 * @mm-patch 0018-Monumenta-Ensure-minecraft-reload-uses-latest-Brigad.patch
 *
 * Remove a bunch of CommandAPI errors
 */
@Mixin(ServerFunctionLibrary.class)
public class ServerFunctionLibraryMixin {
    @Unique
    private static boolean monumenta_mixins$initialFunctionLoad = true;

    @Redirect(
        method = "lambda$reload$1",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResources(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"
        )
    )
    private static Map<ResourceLocation, Resource> disableFunctionOnFirstLoad(FileToIdConverter instance, ResourceManager resourceManager) {
        if (monumenta_mixins$initialFunctionLoad) {
            // lol what
            return new FileToIdConverter("functions", ".nope.nope.nope.nope.Monumenta.nope")
                .listMatchingResources(resourceManager);
        } else {
            return instance.listMatchingResources(resourceManager);
        }
    }

    @Inject(
        method = "lambda$reload$7",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
        )
    )
    private void onReload(Pair<?, ?> intermediate, CallbackInfo ci) {
        monumenta_mixins$initialFunctionLoad = false;
    }
}