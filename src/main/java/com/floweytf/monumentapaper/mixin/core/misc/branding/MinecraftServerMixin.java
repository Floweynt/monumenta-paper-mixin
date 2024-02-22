package com.floweytf.monumentapaper.mixin.core.misc.branding;

import com.floweytf.monumentapaper.PaperPatches;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.MinecraftServer;

/**
 * @author Flowey
 * @mm-patch 0001-Monumenta-MUST-BE-FIRST-Paperweight-build-changes-ba.patch
 *
 * Implement some monumenta specific paper branding changes
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyConstant(
        method = "getServerModName",
        constant = @Constant(stringValue = "Paper")
    )
    private String modifyGetServerModName(String old) {
        return PaperPatches.IDENTIFIER;
    }
}
