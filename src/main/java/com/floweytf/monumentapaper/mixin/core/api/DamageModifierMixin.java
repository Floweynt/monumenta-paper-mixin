package com.floweytf.monumentapaper.mixin.core.api;

import com.floweytf.monumentapaper.Monumenta;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @mm-patch 0027-Monumenta-Handle-iframes-after-damage-event.patch
 * <p>
 * Wanna know a "fun" secret?
 * Java enums aren't real, it's all a myth!
 * Which means we get to do *really* cursed things like this
 * TODO: https://github.com/SpongePowered/Mixin/issues/387
 */
@SuppressWarnings("deprecation")
@Mixin(EntityDamageEvent.DamageModifier.class)
public class DamageModifierMixin {
    @Shadow
    @Final
    @Mutable
    private static DamageModifier[] $VALUES;

    @Invoker("<init>")
    public static DamageModifier monumenta$invokeInit(String internalName, int ord) {
        throw new AssertionError();
    }

    @Unique
    private static DamageModifier monumenta$addVariant(String internalName) {
        if ($VALUES == null)
            throw new AssertionError();

        ArrayList<DamageModifier> variants = new ArrayList<>(Arrays.asList($VALUES));
        var nextOrdinal = variants.get(variants.size() - 1).ordinal() + 1;
        EntityDamageEvent.DamageModifier instance = monumenta$invokeInit(internalName, nextOrdinal);
        variants.add(instance);
        $VALUES = variants.toArray(new DamageModifier[0]);
        return instance;
    }
}