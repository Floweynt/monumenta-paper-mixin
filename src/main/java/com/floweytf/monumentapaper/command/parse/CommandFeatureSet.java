package com.floweytf.monumentapaper.command.parse;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * A set of features that can be enabled during compilation, in order to maintain backwards compatibility.
 */
public class CommandFeatureSet {
    private static final Map<String, FlagSetter> CONSUMERS =
        ImmutableMap.<String, FlagSetter>builder()
            .put("cfv2", (s, f) -> s.v2ControlFlow = f)
            .build();
    private boolean v2ControlFlow = false;

    public boolean isV2ControlFlow() {
        return v2ControlFlow;
    }

    public boolean set(String name, boolean value) {
        final var handler = CONSUMERS.get(name);
        if (handler != null) {
            handler.set(this, value);
            return false;
        }
        return true;
    }

    public boolean enable(String name) {
        return set(name, true);
    }

    public boolean disable(String name) {
        return set(name, false);
    }

    private interface FlagSetter {
        void set(CommandFeatureSet instance, boolean value);
    }
}