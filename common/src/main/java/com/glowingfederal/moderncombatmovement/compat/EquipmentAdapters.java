package com.glowingfederal.moderncombatmovement.compat;

import java.util.ArrayList;
import java.util.List;
/** Optional content stays at this boundary. No UAB classes or units enter core mechanics. */
public final class EquipmentAdapters {
    public interface Adapter {
        boolean supports(String registryName);
        double attackSpeed(String registryName);
    }
    private static final List<Adapter> ADAPTERS = new ArrayList<Adapter>();
    private EquipmentAdapters() {}
    public static void register(Adapter adapter) { if (adapter == null) throw new NullPointerException("adapter"); ADAPTERS.add(adapter); }
    public static double attackSpeed(String registryName, double fallback) {
        for (Adapter adapter : ADAPTERS) if (adapter.supports(registryName)) return adapter.attackSpeed(registryName);
        return fallback;
    }
}
