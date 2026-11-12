package com.glowingfederal.moderncombatmovement.knockback;

public final class KnockbackMath {
    private KnockbackMath() {}
    /** Caller resolves the vanilla random direction fallback and applies velocity exactly once. */
    public static double[] apply(double x, double y, double z, double strength, double resistance, double ratioX, double ratioZ, boolean ground) {
        double power = strength * (1 - resistance);
        if (power <= 0) return new double[] {x,y,z};
        double length = Math.sqrt(ratioX*ratioX + ratioZ*ratioZ);
        if (length < 1.0E-8) throw new IllegalArgumentException("Resolve the vanilla random direction before knockback");
        return new double[] {x/2-ratioX/length*power, ground ? Math.min(0.4, y/2+power) : y, z/2-ratioZ/length*power};
    }
}
