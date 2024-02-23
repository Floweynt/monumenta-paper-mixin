package com.floweytf.monumentapaper.mixin.core.misc;

import com.floweytf.monumentapaper.api.MiscConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.util.ServerShutdownThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Flowey
 * @mm-patch 0009-Monumenta-Increase-shutdown-grace-period-to-20s.patch
 *
 * Make shutdown grace period configurable
 */
@Mixin(ServerShutdownThread.class)
public class ServerShutdownThreadMixin {
    @ModifyConstant(
        method = "run",
        constant = @Constant(intValue = 1000)
    )
    private int monumenta$modifyWaitTime(int constant) {
        return MiscConfiguration.getServerShutdownTime();
    }
}
