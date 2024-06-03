package com.floweytf.monumentapaper;

import net.minecraft.commands.execution.EntryAction;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.UnboundEntryAction;

// TODO: remove
public class MyEntryAction<T> implements EntryAction<T> {
    private final T source;
    private final UnboundEntryAction<T> unbound;

    public MyEntryAction(T source, UnboundEntryAction<T> unbound) {
        this.source = source;
        this.unbound = unbound;
    }

    @Override
    public void execute(ExecutionContext<T> context, Frame frame) {
        unbound.execute(source, context, frame);
    }

    @Override
    public String toString() {
        return unbound + " [" + source + "]";
    }
}
