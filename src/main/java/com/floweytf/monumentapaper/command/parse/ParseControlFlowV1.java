package com.floweytf.monumentapaper.command.parse;

import com.floweytf.monumentapaper.command.ControlFlowEvalSource;
import com.floweytf.monumentapaper.command.functionir.BranchInstr;
import com.floweytf.monumentapaper.command.functionir.CallInstr;
import com.floweytf.monumentapaper.util.ExecuteCommandUtils;
import com.floweytf.monumentapaper.util.Mutable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public class ParseControlFlowV1 implements ISubParser {
    private static final CommandDispatcher<CommandSourceStack> CONTROL_FLOW_V1 = new CommandDispatcher<>();

    public static void register(CommandBuildContext access) {
        ExecuteCommandUtils.registerV1ControlFlow(CONTROL_FLOW_V1, access, "run", context -> {
            final var source = (ControlFlowEvalSource) context.getSource();
            source.getExecState().stack.peekSourceList().add(context.getSource());
            return 0;
        });

        ExecuteCommandUtils.registerV1ControlFlow(CONTROL_FLOW_V1, access, "loop", context -> {
            final var source = (ControlFlowEvalSource) context.getSource();
            source.getExecState().stack.peekSourceList().add(context.getSource());
            return 0;
        });
    }

    private final ParserState state;

    public ParseControlFlowV1(ParserState state) {
        this.state = state;
    }

    /**
     * Parses and code-generates a {@code run ... { ... }} statement
     * <p>
     * <h3>Pseudocode</h3>
     * <pre>
     * {@code
     * var commandSources = runSelector();
     * for(var source : commandSources) {
     *     runBody();
     * }
     * }
     * </pre>
     * <h3>Pseudocode (assembly)</h3>
     * <pre>
     * {@code
     *   PUSH[Source](%source)
     *   PUSH[SourceList](runSelectors())
     * loop_begin:
     *   %0 = PEEK[Source](%source)
     *   BR_COND(%0.isEmpty(), &loop_exit)
     *   %source = %0.popFront()
     *
     *   // ... 'run' statement body
     *
     *   BR(&loop_begin)
     * loop_exit:
     *   POP[SourceList]()
     *   %source = POP[Source]()
     * }
     * </pre>
     *
     * @param reader The line reader that contains the command.
     * @param lineNo The zero-index line number of this statement.
     */
    private void handleV1Run(StringReader reader, int lineNo) {
        final var res = state.parseCommand(
            CONTROL_FLOW_V1, reader,
            msg -> state.reportErr(lineNo, Diagnostic.ERR_CF_V1_RUN, msg)
        );

        if (res.isEmpty()) {
            return;
        }

        final var action = res.get();
        final var loopExitLabel = new Mutable<>(-1);

        state.addControlInstr("cfv1::run::initialize", (state, context, frame) -> {
            state.stack.pushSource(state.source);
            state.stack.pushSourceList(new ArrayList<>());
            action.execute(new ControlFlowEvalSource(state.source, state), context, frame);
        });

        final var loopBeginLabel = state.nextInstrIndex();

        state.addControlInstr("cfv1::run::run_with_entry", (state, context, frame) -> {
            if (state.stack.peekSourceList().isEmpty()) {
                state.instr = loopExitLabel.get();
                return;
            }

            final List<CommandSourceStack> list = state.stack.popSourceList();
            state.stack.pushSourceList(list.subList(1, list.size()));
            state.source = list.get(0);
        });

        state.pushScope(
            () -> state.reportErr(lineNo, Diagnostic.ERR_UNCLOSED),
            () -> {
                state.addCommand(new BranchInstr<>(loopBeginLabel));
                loopExitLabel.set(state.nextInstrIndex());
                state.addControlInstr("cfv1::run::cleanup_exit", (state, context, frame) -> {
                    state.stack.popSourceList();
                    state.source = state.stack.popSource();
                });
            }
        );
    }

    /**
     * Parses and code-generates a {@code loop ... { ... }} statement. This is a bit more involved than {@code run},
     * since it's recursive.
     * <p>
     * <h3>Pseudocode</h3>
     * <pre>
     * {@code
     * void anon() {
     *     var commandSources = runSelector();
     *     for(var source : commandSources) {
     *         runBody();
     *         anon();
     *     }
     * }
     * }
     * </pre>
     * <h3>Pseudocode (assembly)</h3>
     * <pre>
     * {@code
     *   PUSH[InstrAddress](&end)
     * wrapper_function:
     *   PUSH[Source](%source)
     *   PUSH[SourceList](runSelectors())
     * loop_begin:
     *   %0 = PEEK[Source](%source)
     *   BR_COND(%0.isEmpty(), &loop_exit)
     *   %source = %0.popFront()
     *
     *   // ... 'run' statement body
     *   CALL(wrapper_function)
     *
     *   BR(&loop_begin)
     * loop_exit:
     *   POP[SourceList]()
     *   %source = POP[Source]()
     *   RET()
     *
     * end:
     * }
     * </pre>
     *
     * @param reader The line reader that contains the command.
     * @param lineNo The zero-index line number of this statement.
     */
    private void handleV1Loop(StringReader reader, int lineNo) {
        final var res = state.parseCommand(
            CONTROL_FLOW_V1, reader,
            msg -> state.reportErr(lineNo, Diagnostic.ERR_CF_V1_LOOP, msg)
        );

        if (res.isEmpty()) {
            return;
        }

        final var action = res.get();
        final var loopExitLabel = new Mutable<>(-1);
        final var endLabel = new Mutable<>(-1);

        state.addControlInstr("cfv1::loop_pseudo_call", (state, context, frame) -> {
            state.stack.pushInstrAddress(endLabel.get());
        });

        final var wrapperFunctionLabel = state.nextInstrIndex();

        state.addControlInstr("cfv1::loop::initialize", (state, context, frame) -> {
            state.stack.pushSource(state.source);
            state.stack.pushSourceList(new ArrayList<>());
            action.execute(new ControlFlowEvalSource(state.source, state), context, frame);
        });

        final var loopBeginLabel = state.nextInstrIndex();

        state.addControlInstr("cfv1::loop::run_with_entry", (state, context, frame) -> {
            if (state.stack.peekSourceList().isEmpty()) {
                state.instr = loopExitLabel.get();
                return;
            }

            final List<CommandSourceStack> list = state.stack.popSourceList();
            state.stack.pushSourceList(list.subList(1, list.size()));
            state.source = list.get(0);
        });

        state.pushScope(
            () -> state.reportErr(lineNo, Diagnostic.ERR_UNCLOSED),
            () -> {
                state.addCommand(new CallInstr<>(wrapperFunctionLabel));
                state.addCommand(new BranchInstr<>(loopBeginLabel));
                loopExitLabel.set(state.nextInstrIndex());
                state.addControlInstr("cfv1::loop::cleanup_exit", (state, context, frame) -> {
                    state.stack.popSourceList();
                    state.source = state.stack.popSource();
                    state.instr = state.stack.popInstrAddress();
                });
                endLabel.set(state.nextInstrIndex());
            }
        );
    }

    @Override
    public boolean parse(String unquotedWord, StringReader reader, int lineNo) {
        if (state.features().isV2ControlFlow()) {
            return false;
        }

        switch (unquotedWord) {
        case "run":
            handleV1Run(reader, lineNo);
            return true;
        case "loop":
            handleV1Loop(reader, lineNo);
            return true;
        }

        if (reader.getString().equals("}")) {
            if (!state.hasScope()) {
                state.reportErr(lineNo, Diagnostic.ERR_CF_V1_EXTRA_CLOSE);
                return true;
            }

            state.runOnClose();
            return true;
        }

        return false;
    }
}