package com.floweytf.monumentapaper.command.parse;

import com.mojang.brigadier.StringReader;

public interface ISubParser {
    boolean parse(String unquotedWord, StringReader reader, int lineNo);
}
