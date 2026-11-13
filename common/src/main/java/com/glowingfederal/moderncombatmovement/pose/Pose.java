package com.glowingfederal.moderncombatmovement.pose;

/** Crawling is the swimming-sized pose without the swimming movement flag. Wire ordinals are stable. */
public enum Pose {
    STANDING(1.8F, 1.62F), CROUCHING(1.5F, 1.27F), LOW(0.6F, 0.4F), FALL_FLYING(0.6F, 0.4F);
    public final float height, eyeHeight;
    Pose(float height, float eyeHeight) { this.height = height; this.eyeHeight = eyeHeight; }
}
