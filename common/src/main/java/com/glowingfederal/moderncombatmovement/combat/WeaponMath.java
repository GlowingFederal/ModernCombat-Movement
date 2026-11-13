package com.glowingfederal.moderncombatmovement.combat;

/** Verified 1.21.11 weapon, armor and blast arithmetic; no engine or optional-mod references. */
public final class WeaponMath {
    private WeaponMath() {}

    public static double smashBonus(double fallDistance, int density) {
        if (fallDistance <= 1.5) return 0;
        double base = fallDistance <= 3 ? 4 * fallDistance
                : fallDistance <= 8 ? 12 + 2 * (fallDistance - 3) : 22 + fallDistance - 8;
        return base + .5 * Math.max(0, density) * fallDistance;
    }

    public static double smashKnockback(double distance, double fallDistance, double resistance) {
        return Math.max(0, (3.5 - distance) * .7F * (fallDistance > 5 ? 2 : 1)
                * (1 - Math.max(0, Math.min(1, resistance))));
    }

    public static float armor(float damage, double points, double toughness, int breach) {
        double effective = Math.max(points * .2, Math.min(20, points - damage / (2 + toughness / 4)));
        effective *= Math.max(0, Math.min(1, 1 - .15 * breach));
        return (float) (damage * (1 - effective / 25));
    }

    public static int explosionDamage(double normalizedDistance, double exposure, float radius) {
        if (normalizedDistance > 1) return 0;
        double impact = (1 - normalizedDistance) * exposure;
        return (int) ((impact * impact + impact) / 2 * 7 * radius * 2 + 1);
    }

    public static double windBurstMultiplier(int level) {
        return level <= 0 ? 0 : level == 1 ? 1.2 : level == 2 ? 1.75 : level == 3 ? 2.2 : 1.5 + .35 * (level - 1);
    }
}
