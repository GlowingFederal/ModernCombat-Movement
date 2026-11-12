package com.glowingfederal.moderncombatmovement.pose;

public final class PoseRules {
    private PoseRules() {}
    /** Player.updatePlayerPose: never force an expansion into an occupied volume. */
    public static Pose resolve(Pose previous, Pose wanted, boolean fitsWanted, boolean fitsCrouching, boolean fitsLow) {
        if (!fitsLow) return previous;
        if (fitsWanted) return wanted;
        return fitsCrouching ? Pose.CROUCHING : Pose.LOW;
    }
    /** Entity.updateSwimming: entering requires submerged eyes; continuing only requires touching water. */
    public static boolean swimming(boolean previous, boolean sprinting, boolean touchingWater, boolean eyesInWater, boolean passenger, boolean flying) {
        return !passenger && !flying && sprinting && touchingWater && (previous || eyesInWater);
    }
}
