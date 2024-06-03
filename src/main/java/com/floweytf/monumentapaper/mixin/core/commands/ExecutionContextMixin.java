package com.floweytf.monumentapaper.mixin.core.commands;

import net.minecraft.commands.execution.CommandQueueEntry;
import net.minecraft.commands.execution.ExecutionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Deque;

@Mixin(ExecutionContext.class)
public class ExecutionContextMixin {
    // TODO: remove
    @Redirect(
        method = "runCommandQueue",
        at = @At(value = "INVOKE", target = "Ljava/util/Deque;pollFirst()Ljava/lang/Object;")
    )
    private Object monumenta$logEntryTemp(Deque<CommandQueueEntry<?>> instance) {
        final var obj = instance.pollFirst();
        if (obj != null) {
            System.out.println("Running: " + obj.action());
        }
        return obj;
    }
}
