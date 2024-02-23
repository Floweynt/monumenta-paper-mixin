package com.floweytf.monumentapaper;

import java.io.PrintStream;

public class EarlyConstants {
    public static PrintStream out;

    private static final boolean ENABLE_DEBUG_PRINT = true;

    public static void println(String str) {
        if(ENABLE_DEBUG_PRINT) {
            out.println("[CorePluginMixinService; debug]: " + str);
        }
    }
}