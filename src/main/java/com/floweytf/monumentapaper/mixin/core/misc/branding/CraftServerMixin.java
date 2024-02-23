package com.floweytf.monumentapaper.mixin.core.misc.branding;

import com.floweytf.monumentapaper.PaperPatches;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

/**
 * @author Flowey
 * @mm-patch 0001-Monumenta-MUST-BE-FIRST-Paperweight-build-changes-ba.patch
 *
 * Implement some monumenta specific paper branding changes
 */
@Mixin(CraftServer.class)
public class CraftServerMixin {
    @ModifyConstant(
        method = "<init>",
        constant = @Constant(stringValue = "Paper")
    )
    private String monumenta$modifyServerName(String string) {
        return PaperPatches.IDENTIFIER + string;
    }

    @ModifyExpressionValue(
        method = "<init>",
        at = @At(
            target = "Ljava/lang/Package;getImplementationVersion()Ljava/lang/String;",
            value = "INVOKE"
        )
    )
    private String monumenta$modifyImplementationVersion(String original) {
        return original.replace("Paper", PaperPatches.IDENTIFIER + "Paper");
    }
}
