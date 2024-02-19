package com.floweytf.monumentamixins.mixin.core;

import com.floweytf.monumentamixins.mixin.PaperPatches;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftServer.class)
public class CraftServerMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "Paper"), require = 1)
    private String modifyServerName(String string) {
        return "MonumentaPaper";
    }

    @Redirect(method = "<init>", at = @At(target = "Ljava/lang/Package;getImplementationVersion()Ljava/lang/String;", value = "INVOKE"), require = 1)
    private String modifyImplementationVersion(Package pack) {
        return pack.getImplementationVersion().replace("Paper", PaperPatches.IDENTIFIER + "Paper");
    }
}
