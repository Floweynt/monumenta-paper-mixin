package com.floweytf.monumentapaper.mixin.core.commands;

import com.floweytf.monumentapaper.command.parse.ParseControlFlowV1;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Inject(
        method = "<init>(Lnet/minecraft/commands/Commands$CommandSelection;" +
            "Lnet/minecraft/commands/CommandBuildContext;)V",
        at = @At("TAIL")
    )
    private void monumenta$registerCommands(Commands.CommandSelection environment, CommandBuildContext access,
                                            CallbackInfo ci) {
        ParseControlFlowV1.register(access);
    }
}
