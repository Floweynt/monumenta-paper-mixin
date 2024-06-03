package com.floweytf.monumentapaper.command.parse;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.network.chat.Component;

import static com.floweytf.monumentapaper.util.CommandUtil.arg;
import static com.floweytf.monumentapaper.util.CommandUtil.lit;

public class ParsePragma implements ISubParser {
    /**
     * A command dispatcher for handling pragma. Since this is evaluated at compile time, it takes {@code
     * FunctionParser} as the source
     */
    private static final CommandDispatcher<ParserState> DISPATCH = new CommandDispatcher<>();

    static {
        final var PRAGMA_BAD_FLAG =
            new DynamicCommandExceptionType(s -> Component.literal(String.format("Illegal pragma feature flag %s", s)));

        DISPATCH.register(
            lit("pragma",
                lit("enable",
                    arg("flag", StringArgumentType.word(), (context) -> {
                        final var value = StringArgumentType.getString(context, "flag");
                        if (context.getSource().features().enable(StringArgumentType.getString(context, "flag"))) {
                            throw PRAGMA_BAD_FLAG.create(value);
                        }
                        return 0;
                    })
                ),
                lit("disable",
                    arg("flag", StringArgumentType.word(), (context) -> {
                        final var value = StringArgumentType.getString(context, "flag");
                        if (context.getSource().features().disable(StringArgumentType.getString(context, "flag"))) {
                            throw PRAGMA_BAD_FLAG.create(value);
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