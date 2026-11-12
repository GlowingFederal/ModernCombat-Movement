package com.glowingfederal.moderncombatmovement.movement;

/** Pure rules transcribed from the official 1.21.11 reference, not an alternate physics profile. */
public final class MovementMath {
    private MovementMath() {}
    public static boolean stopHorizontal(double x, double z) { return x*x + z*z < 9.0E-6; }
    public static double fluidGravity(double y, boolean falling, double gravity) {
        if (gravity == 0) return y;
        return falling && Math.abs(y - 0.005) >= 0.003 && Math.abs(y - gravity / 16) < 0.003 ? -0.003 : y - gravity / 16;
    }
    public static double swimmingY(double y, double lookY) {
        return y + (lookY - y) * (lookY < -0.2 ? 0.085 : 0.06);
    }
    public static float groundAcceleration(float speed, float slipperiness) {
        return speed * (0.21600002F / (slipperiness * slipperiness * slipperiness));
    }
    public static boolean minorCollision(double inputX, double inputZ, double moveX, double moveZ) {
        double a = inputX*inputX + inputZ*inputZ, b = moveX*moveX + moveZ*moveZ;
        return a >= 1.0E-5F && b >= 1.0E-5F && Math.acos(Math.max(-1, Math.min(1, (inputX*moveX + inputZ*moveZ) / Math.sqrt(a*b)))) < 0.13962634F;
    }
}
