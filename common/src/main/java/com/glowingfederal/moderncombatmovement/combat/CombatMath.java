package com.glowingfederal.moderncombatmovement.combat;

/** Shared combat foundation. The legacy attack pipeline is deliberately not wired until damage/enchantment ownership is migrated. */
public final class CombatMath {
    private CombatMath() {}
    public static float strength(int ticks, float partial, double speed) {
        return (float)Math.max(0, Math.min(1, (ticks + partial) * speed / 20));
    }
    public static float baseScale(float strength) { return 0.2F + strength*strength*0.8F; }
    public static float damage(float base, float enchantedBonus, float strength, boolean critical) {
        return base * baseScale(strength) * (critical ? 1.5F : 1) + enchantedBonus * strength;
    }
    public static boolean critical(float strength, double fallDistance, boolean ground, boolean climbable, boolean water, boolean blind, boolean passenger, boolean sprinting, boolean livingTarget) {
        return strength > 0.9F && fallDistance > 0 && !ground && !climbable && !water && !blind && !passenger && !sprinting && livingTarget;
    }
    public static double sweepRatio(int level) { return level > 0 ? (double)level / (level + 1) : 0; }
}
