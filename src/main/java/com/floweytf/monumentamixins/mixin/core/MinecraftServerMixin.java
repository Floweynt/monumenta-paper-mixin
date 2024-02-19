package com.floweytf.monumentamixins.mixin.core;

import com.floweytf.monumentamixins.mixin.PaperPatches;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(method = "getServerModName", constant = @Constant(stringValue = "Paper"))
    private String modifyGetServerModName(String old) {
        return PaperPatches.IDENTIFIER;
    }
}
