package com.floweytf.monumentapaper.command.parse;

import com.floweytf.monumentapaper.util.ComponentUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.floweytf.monumentapaper.util.CommandUtil.*;

public class ParsePragma implements ISubParser {
    /**
     * A command dispatcher for handling pragma. Since this is evaluated at compile time, it takes {@code
     * FunctionParser} as the source
     */
    private static final CommandDispatcher<ParserState> DISPATCH = new CommandDispatcher<>();

    static {
        final var PRAGMA_BAD_FEAT = exceptionType(s -> ComponentUtils.fLiteral(Diagnostic.ERR_PRAGMA_BAD_FEAT, s));

        final var PRAGMA_SCOPE_NOT_EMPTY = exceptionType(Diagnostic.ERR_PRAGMA_SCOPE_NOT_EMPTY);

        DISPATCH.register(
            lit("pragma",
                lit("enable",
                    arg("flag", StringArgumentType.word(), (context) -> {
                        if (context.getSource().hasScope()) {
                            throw PRAGMA_SCOPE_NOT_EMPTY.create();
                        }

                        final var value = StringArgumentType.getString(context, "flag");
                        if (context.getSource().features().enable(StringArgumentType.getString(context, "flag"))) {
                            throw PRAGMA_BAD_FEAT.create(value);
                        }
                        return 0;
                    })
                ),
                lit("disable",
                    arg("flag", StringArgumentType.word(), (context) -> {
                        if (context.getSource().hasScope()) {
                            throw PRAGMA_SCOPE_NOT_EMPTY.create();
                        }

                        final var value = StringArgumentType.getString(context, "flag");
                        if (context.getSource().features().disable(StringArgumentType.getString(context, "flag"))) {
                            throw PRAGMA_BAD_FEAT.create(value);
                        }
                        return 0;
                    })
                )
            )
        );
    }

    private final ParserState state;

    public ParsePragma(ParserState state) {
        this.state = state;
    }

    @Override
    public boolean parse(String unquotedWord, StringReader reader, int lineNo) {
        if (!unquotedWord.equals("pragma")) {
            return false;
        }

        try {
            DISPATCH.execute(reader, state);
        } catch (CommandSyntaxException exception) {
            state.reportErr(lineNo, Diagnostic.ERR_PRAGMA, exception.getMessage());
        }

        return true;
    }
}