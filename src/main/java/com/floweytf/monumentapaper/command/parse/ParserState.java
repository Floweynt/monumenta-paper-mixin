package com.floweytf.monumentapaper.command.parse;

import com.floweytf.monumentapaper.Monumenta;
import com.floweytf.monumentapaper.command.functionir.ControlInstr;
import com.floweytf.monumentapaper.command.functionir.FuncExecState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.functions.FunctionBuilder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ParserState {
    private static final Logger LOGGER = LogManager.getLogger(Monumenta.IDENTIFIER + "/FunctionParser");
    private static final int CONTEXT = 2;

    private record ScopeExit(Runnable onUnclosed, Runnable onClose) {
    }

    private final CommandFeatureSet features = new CommandFeatureSet();
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final CommandSourceStack dummySource;
    private final ArrayDeque<ScopeExit> scopeStack = new ArrayDeque<>();
    private final FunctionBuilder<CommandSourceStack> builder = new FunctionBuilder<>();
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public ParserState(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack dummySource) {
        this.dispatcher = dispatcher;
        this.dummySource = dummySource;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }

    private void report(DiagnosticLevel level, int line, String format, Object... args) {
        diagnostics.add(new Diagnostic(level, line, String.format(format, args)));
    }

    public void reportErr(int line, String format, Object... args) {
        report(DiagnosticLevel.ERROR, line, format, args);
    }

    public void reportWarn(int line, String format, Object... args) {
        report(DiagnosticLevel.WARN, line, format, args);
    }

    public <T extends ControlInstr<CommandSourceStack>> T addControlInstr(String name, T controlInstr) {
        builder.addCommand(new ControlInstr<>() {
            @Override
            public void execute(@NotNull CommandSourceStack source,
                                @NotNull ExecutionContext<CommandSourceStack> context, @NotNull Frame frame) {
                controlInstr.execute(source, context, frame);
            }

            @Override
            public void modifyState(FuncExecState<CommandSourceStack> state,
                                    ExecutionContext<CommandSourceStack> context, Frame frame) {
                controlInstr.modifyState(state, context, frame);
            }

            @Override
            public String toString() {
                return name;
            }
        });

        return controlInstr;
    }

    public void addCommand(UnboundEntryAction<CommandSourceStack> stack) {
        builder.addCommand(stack);
    }

    public void addMacro(String command, int lineNo) {
        builder.addMacro(command, lineNo);
    }

    public int nextInstrIndex() {
        return builder.macroEntries == null ? builder.plainEntries.size() : builder.macroEntries.size();
    }

    public Optional<UnboundEntryAction<CommandSourceStack>> parseCommand(CommandDispatcher<CommandSourceStack> dispatcher, StringReader reader, Consumer<String> onError) {
        final var parseResults = dispatcher.parse(reader, dummySource);

        try {
            Commands.validateParseResults(parseResults);
        } catch (CommandSyntaxException e) {
            onError.accept(e.getMessage());
            return Optional.empty();
        }

        final var chain = ContextChain.tryFlatten(parseResults.getContext().build(reader.getString()));

        if (chain.isEmpty()) {
            onError.accept(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader()).getMessage());
            return Optional.empty();
        }

        return chain.map(x -> new BuildContexts.Unbound<>(reader.getString(), x));
    }

    public boolean dumpErrors(ResourceLocation id, List<String> lines) {
        boolean hasError = false;

        if (diagnostics.isEmpty())
            return false;

        StringBuilder builder = new StringBuilder();
        builder.append("While parsing function '").append(id).append("'\n");

        for (final var diagnostic : diagnostics) {
            if (diagnostic.level() == DiagnosticLevel.ERROR) {
                hasError = true;
            }

            builder.append(diagnostic.level())
                .append(" (")
                .append(id)
                .append(":")
                .append(diagnostic.line() + 1)
                .append("): ")
                .append(diagnostic.message())
                .append("\n");

            List<IntObjectPair<String>> lineEntry = new ArrayList<>();
            for (int i = diagnostic.line() - CONTEXT; i <= diagnostic.line() + CONTEXT; i++) {
                if (i >= 0 && i < lines.size()) {
                    lineEntry.add(IntObjectPair.of(i + 1, lines.get(i)));
                }
            }

            final var pad = Integer.toString(lineEntry.get(lineEntry.size() - 1).firstInt()).length();
            for (var entry : lineEntry) {
                boolean isErrLine = entry.firstInt() == diagnostic.line() + 1;
                builder.append(String.format("%-" + pad + "d", entry.firstInt()))
                    .append(isErrLine ? " * " : " | ")
                    .append(entry.value());

                if (isErrLine) {
                    builder.append(" <- HERE");
                }

                builder.append("\n");
            }
        }

        LOGGER.warn(builder);

        return hasError;
    }

    public CommandFeatureSet features() {
        return features;
    }

    public void pushScope(Runnable onUnclosed, Runnable onClose) {
        scopeStack.push(new ScopeExit(onUnclosed, onClose));
    }

    public boolean hasScope() {
        return !scopeStack.isEmpty();
    }

    public void runOnClose() {
        scopeStack.pop().onClose.run();
    }

    public void runOnUnclosed() {
        scopeStack.pop().onUnclosed.run();
    }

    public FunctionBuilder<CommandSourceStack> builder() {
        return builder;
    }
}