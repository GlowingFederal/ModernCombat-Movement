package com.glowingfederal.moderncombatmovement.combat;

public final class CrossbowMath {
    private CrossbowMath() {}
    public static int chargeTicks(int quickCharge) { return 25 - 5 * Math.min(3, Math.max(0, quickCharge)); }
    /** Rotate around the shooter's up vector, rather than world Y, for pitched multishot. */
    public static double[] direction(float yawDegrees, float pitchDegrees, double spreadDegrees) {
        double yaw = Math.toRadians(yawDegrees), pitch = Math.toRadians(pitchDegrees);
        double x = -Math.sin(yaw) * Math.cos(pitch), y = -Math.sin(pitch), z = Math.cos(yaw) * Math.cos(pitch);
        double ux = -Math.sin(yaw) * Math.sin(pitch), uy = Math.cos(pitch), uz = Math.cos(yaw) * Math.sin(pitch);
        double c = Math.cos(Math.toRadians(spreadDegrees)), s = Math.sin(Math.toRadians(spreadDegrees));
        return new double[] {x*c + (uy*z-uz*y)*s, y*c + (uz*x-ux*z)*s, z*c + (ux*y-uy*x)*s};
    }
}
