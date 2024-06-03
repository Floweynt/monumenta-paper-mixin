package com.floweytf.monumentapaper.mixin.core.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.execution.tasks.ExecuteCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExecuteCommand.class)
public class ExecuteCommandMixin<T> {
    // TODO: remove
    @Shadow
    @Final
    private String commandInput;

    @Shadow
    @Final
    private CommandContext<T> executionContext;

    @Override
    public String toString() {
        return "(exec) " + commandInput + "[" + executionContext.getSource() + "]";
    }
}
