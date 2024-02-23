package com.floweytf.monumentapaper;

import org.spongepowered.asm.mixin.Unique;

import java.io.File;
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

    // we create this dummy file object that never exists
    // this tricks MC into not reading from file
    @Unique
    public static final File FAKE_FILE = new File("") {
        @Override
        public boolean exists() {
            return false;
        }
    };
}
