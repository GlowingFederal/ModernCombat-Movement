package com.glowingfederal.moderncombatmovement.compat;

import com.glowingfederal.moderncombatmovement.api.CombatApi;
import com.glowingfederal.moderncombatmovement.api.ItemProperties;
import com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind;

public final class VanillaProfiles {
    private VanillaProfiles() {}
    public static void register() {
        add("minecraft:air", Kind.HAND, 4, 1, false, false);
        String[] materials = {"wooden", "stone", "iron", "golden", "diamond"};
        double[] sword = {4, 5, 6, 4, 7}, axe = {7, 9, 9, 7, 9}, axeSpeed = {.8, .8, .9, 1, 1};
        double[] tool = {0, 1, 2, 0, 3}, hoeSpeed = {1, 2, 3, 1, 4};
        for (int i = 0; i < materials.length; i++) {
            String prefix = "minecraft:" + materials[i] + "_";
            add(prefix + "sword", Kind.SWORD, 1.6, sword[i], true, false);
            add(prefix + "axe", Kind.AXE, axeSpeed[i], axe[i], false, false);
            add(prefix + "pickaxe", Kind.TOOL, 1.2, 2 + tool[i], false, false);
            add(prefix + "shovel", Kind.TOOL, 1, 2.5 + tool[i], false, false);
            add(prefix + "hoe", Kind.TOOL, hoeSpeed[i], 1, false, false);
        }
        add("minecraft:bow", Kind.BOW, 4, 1, false, true);
        add("minecraft:shield", Kind.SHIELD, 4, 1, false, false);
    }
    private static void add(String id, Kind kind, double speed, double damage, boolean sweep, boolean projectile) {
        CombatApi.register(id, new ItemProperties(kind, speed, damage, sweep, projectile));
    }
}
