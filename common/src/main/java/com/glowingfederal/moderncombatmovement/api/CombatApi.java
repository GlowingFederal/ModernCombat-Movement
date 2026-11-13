package com.glowingfederal.moderncombatmovement.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Register during Forge preInit/init/postInit, before FMLLoadCompleteEvent.
 * Unknown IDs have no profile and retain their owning mod's attack/projectile pipeline.
 * Registration never matches subclasses, suffixes, ore dictionary entries or namespaces.
 */
public final class CombatApi {
    private static final Map<String, ItemProperties> ITEMS = new LinkedHashMap<String, ItemProperties>();
    private static boolean frozen;

    private CombatApi() {}

    public static synchronized void register(String registryId, ItemProperties properties) {
        validate(registryId, properties);
        if (ITEMS.containsKey(registryId)) throw new IllegalArgumentException("Already registered: " + registryId);
        ITEMS.put(registryId, properties);
    }

    /** Deliberate override for compatibility mods; returns the previous profile for diagnostics. */
    public static synchronized ItemProperties replace(String registryId, ItemProperties properties) {
        validate(registryId, properties);
        return ITEMS.put(registryId, properties);
    }

    private static void validate(String id, ItemProperties properties) {
        if (frozen) throw new IllegalStateException("Combat registrations are frozen after mod loading");
        if (id == null || !id.matches("[a-z0-9_.-]+:[A-Za-z0-9_./-]+") || properties == null) {
            throw new IllegalArgumentException("An exact namespaced registry ID and properties are required");
        }
    }

    public static synchronized ItemProperties find(String registryId) { return ITEMS.get(registryId); }
    public static synchronized Map<String, ItemProperties> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, ItemProperties>(ITEMS));
    }
    public static synchronized void freeze() { frozen = true; }
}
