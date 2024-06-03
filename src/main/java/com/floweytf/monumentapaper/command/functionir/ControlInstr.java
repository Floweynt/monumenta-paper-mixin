package com.floweytf.monumentapaper.command.functionir;

import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.UnboundEntryAction;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a special control flow instruction within a "compiled" MCFunction.
 *
 * @param <T> The type, usually {@link net.minecraft.commands.CommandSourceStack}
 * @see FuncExecState
 */
@FunctionalInterface
public interface ControlInstr<T> extends UnboundEntryAction<T> {
    @Override
    default void execute(@NotNull T source, @NotNull ExecutionContext<T> context, @NotNull Frame frame) {
    }

    /**
     * Perform special state modification operations.
     *
     * @param state   The current state, with {@link FuncExecState#instr} pointing to the <b>next</b> instr.
     * @param context The current execution context, same as {@link UnboundEntryAction#execute}.
     * @param frame   The current frame, same as {@link UnboundEntryAction#execute}.
     */
    void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame);
}