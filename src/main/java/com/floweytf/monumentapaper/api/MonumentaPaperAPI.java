package com.floweytf.monumentapaper.api;

import com.floweytf.monumentapaper.Monumenta;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings("deprecation")
public class MonumentaPaperAPI {
    /**
     * Don't worry about how this enumerator has an instance without patching API jar...
     */
    public static final EntityDamageEvent.DamageModifier IFRAMES = EntityDamageEvent.DamageModifier.valueOf("IFRAMES");

    private static int flyingTickTime = 120;
    private static int serverShutdownTime = 20000;

    public static int getFlyingTickTime() {
        return flyingTickTime;
    }

    // actually useful API functions
    public static void setFlyingTickTime(int flyingTickTime) {
        MonumentaPaperAPI.flyingTickTime = Math.max(0, flyingTickTime);
    }

    public static int getServerShutdownTime() {
        return serverShutdownTime;
    }

    public static void setServerShutdownTime(int flyingTickTime) {
        MonumentaPaperAPI.serverShutdownTime = Math.max(1000, flyingTickTime);
    }

    public static String getIdentifier() {
        return Monumenta.getIdentifier();
    }
}
