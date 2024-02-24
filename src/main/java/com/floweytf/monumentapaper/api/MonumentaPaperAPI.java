package com.floweytf.monumentapaper.api;

import com.floweytf.monumentapaper.Monumenta;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings("deprecation")
public class MonumentaPaperAPI {
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
        MonumentaPaperAPI.flyingTickTime = Math.max(0, flyingTickTime);
    }

    public static void setServerShutdownTime(int flyingTickTime) {
        MonumentaPaperAPI.serverShutdownTime = Math.max(1000, flyingTickTime);
    }

    public static final EntityDamageEvent.DamageModifier IFRAMES = Monumenta.IFRAMES;
}
