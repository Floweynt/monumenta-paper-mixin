package com.floweytf.monumentapaper.command.parse;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

/**
 * A parser for MCFunctions, with control flow capabilities.
 */
public class FunctionParser {
    private final ParserState state;
    private final List<ISubParser> parsers;

    private void parseLine(StringReader reader, int lineNo) {
        // parse macros
        if (reader.peek() == '$') {
            state.addMacro(reader.getString().substring(1), lineNo);
            return;
        }

        final var unquotedWord = new StringReader(reader.getString()).readUnquotedString();

        for (var parser : parsers) {
            if (parser.parse(unquotedWord, reader, lineNo)) {
                return;
            }
        }

        state.parseCommand(
            state.getDispatcher(), reader,
            msg -> state.reportErr(lineNo, Diagnostic.ERR_PARSE_CMD, msg)
        ).ifPresent(state::addCommand);
    }

    private void doParse(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String cmd;

            // Read the next line, handling \ at the end of the line if present
            if (CommandFunction.shouldConcatenateNextLine(line)) {
                StringBuilder reader = new StringBuilder(line);
                do {
                    if (++i == lines.size()) {
                        state.reportErr(i, Diagnostic.ERR_LINE_CONT);
                        return;
                    }

                    reader.deleteCharAt(reader.length() - 1);
                    String string2 = lines.get(i).trim();
                    reader.append(string2);
                } while (CommandFunction.shouldConcatenateNextLine(reader));
                cmd = reader.toString();
            } else {
                cmd = line;
            }

            final var reader = new StringReader(cmd.trim());

            // skip comments and empty lines
            if (!reader.canRead() || reader.peek() == '#') {
                continue;
            }

            // better syntax error handling for lines starting with '/'
            if (reader.peek() == '/') {
                reader.skip();
                if (reader.peek() == '/') {
                    state.reportErr(i, Diagnostic.ERR_BAD_CMD, cmd);
                    continue;
                }

                state.reportErr(i, Diagnostic.ERR_FORWARD_SLASH, cmd, reader.readUnquotedString());
                continue;
            }

            // parse the line
            parseLine(reader, i);
        }

        // report unclosed control flow statements
        while (state.hasScope()) {
            state.runOnUnclosed();
        }
    }

    /**
     * Constructs and parses MCFunction, throwing exceptions if syntax errors are detected.
     *
     * @param dispatcher The command dispatcher to use.
     * @param source     The "dummy" command source to use.
     */
    public FunctionParser(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source) {
        state = new ParserState(dispatcher, source);
        parsers = List.of(
            new ParsePragma(state),
            new ParseControlFlowV1(state)
        );
    }

    public Optional<CommandFunction<CommandSourceStack>> parse(ResourceLocation id, List<String> lines) {
        doParse(lines);
        if (state.dumpErrors(id, lines))
            return Optional.empty();
        return Optional.of(state.builder().build(id));
    }
}