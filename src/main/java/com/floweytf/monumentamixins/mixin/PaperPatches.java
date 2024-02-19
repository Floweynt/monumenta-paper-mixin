package com.floweytf.monumentamixins.mixin;

import java.io.PrintStream;

public class PaperPatches {
    public static PrintStream out;

    private static final boolean ENABLE_DEBUG_PRINT = true;
    public static final String IDENTIFIER = "Monumenta";

    public static void println(String str) {
        if(ENABLE_DEBUG_PRINT) {
            out.println("[CorePluginMixinService; debug]: " + str);
        }
    }
}
