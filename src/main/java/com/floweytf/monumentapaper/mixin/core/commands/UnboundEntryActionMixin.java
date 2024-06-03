package com.floweytf.monumentapaper.mixin.core.commands;

import com.floweytf.monumentapaper.MyEntryAction;
import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.UnboundEntryAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UnboundEntryAction.class)
public interface UnboundEntryActionMixin<T> {
    // TODO: remove

    @Shadow
    void execute(T source, ExecutionContext<T> context, Frame frame);

    /**
     * @author
     * @reason
     */
    @Overwrite
    default EntryAction<T> bind(T source) {
        return new MyEntryAction<T>(source, (UnboundEntryAction) this);
    }
}