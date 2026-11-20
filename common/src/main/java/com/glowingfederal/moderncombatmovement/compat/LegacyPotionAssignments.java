package com.glowingfederal.moderncombatmovement.compat;

import java.util.Properties;

/** Decisions for importing the four pre-ledger legacy potion settings. */
public final class LegacyPotionAssignments {
    private LegacyPotionAssignments() {}

    public static int request(Properties allocations, String owner, String name,
                              int configured, int previousDefault) {
        String key = owner + ":" + name;
        String saved = allocations.getProperty(key);
        if (configured < 0 || configured != previousDefault
                || saved != null && !"-1".equals(saved)) return configured;
        for (String other : allocations.stringPropertyNames()) {
            if (!"schema".equals(other) && !key.equals(other)
                    && Integer.toString(configured).equals(allocations.getProperty(other))) {
                // This default cannot have coexisted with the other saved identity.
                // Keep that identity and allocate the newly imported MCM effect.
                return -1;
            }
        }
        return configured;
    }

    public static boolean ambiguousWorld(Properties snapshot, String owner, String[] legacyNames) {
        boolean owned = false;
        for (String key : snapshot.stringPropertyNames())
            if (key.startsWith(owner + ":")) owned = true;
        if (!owned) return false;
        for (String name : legacyNames)
            if (!snapshot.containsKey(owner + ":" + name)) return true;
        return false;
    }
}
