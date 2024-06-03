package com.floweytf.monumentapaper.command.functionir;

import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;

/**
 * Static jump instruction.
 * <h3>Pseudocode (assembly)</h3>
 * <pre>
 * {@code
 * %ip = target
 * }
 * </pre>
 *
 * @param target The instruction to jump to.
 * @param <T>    The command source.
 */
public record BranchInstr<T>(int target) implements ControlInstr<T> {

    @Override
    public void modifyState(FuncExecState<T> state, ExecutionContext<T> context, Frame frame) {
        state.instr = target;
    }
}
