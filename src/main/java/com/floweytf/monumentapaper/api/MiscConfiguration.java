package com.floweytf.monumentapaper.api;

// misc MC configuration things
public class MiscConfiguration {
    private static int flyingTickTime = 120;
    private static int serverShutdownTime = 20000;

    public static int getFlyingTickTime() {
        return flyingTickTime;
    }

    public static int getServerShutdownTime() {
        return serverShutdownTime;
    }

    // actually useful API functions
    public static void setFlyingTickTime(int flyingTickTime) {
        MiscConfiguration.flyingTickTime = Math.max(0, flyingTickTime);
    }

    public static void setServerShutdownTime(int flyingTickTime) {
        MiscConfiguration.serverShutdownTime = Math.max(1000, flyingTickTime);
    }
}
