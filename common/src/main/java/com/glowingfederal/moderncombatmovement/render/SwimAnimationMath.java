package com.glowingfederal.moderncombatmovement.render;

/** Native swimming stroke used by Combatives' legacy biped adaptation. Phase follows travelled movement. */
public final class SwimAnimationMath {
    private SwimAnimationMath() {}

    public static void stroke(float limb, float[] angles) {
        float phase = (limb % 26 + 26) % 26;
        float pi = (float) Math.PI;
        float x, leftZ, rightZ;
        if (phase < 14) {
            float bend = (-65 * phase + phase * phase) / (-65 * 14F + 14F * 14F);
            x = 0;
            leftZ = pi + 1.8707964F * bend;
            rightZ = pi - 1.8707964F * bend;
        } else if (phase < 22) {
            float progress = (phase - 14) / 8;
            x = pi / 2 * progress;
            leftZ = 5.012389F - 1.8707964F * progress;
            rightZ = 1.2707963F + 1.8707964F * progress;
        } else {
            x = pi / 2 * (1 - (phase - 22) / 4);
            leftZ = rightZ = pi;
        }
        angles[0] = x;
        angles[1] = leftZ;
        angles[2] = rightZ;
        angles[3] = .3F * (float) Math.cos(limb / 3 + pi);
        angles[4] = .3F * (float) Math.cos(limb / 3);
    }

    public static float angle(float from, float to, float amount) {
        float delta = (to - from) % ((float) Math.PI * 2);
        if (delta < -Math.PI) delta += (float) Math.PI * 2;
        if (delta >= Math.PI) delta -= (float) Math.PI * 2;
        return from + delta * amount;
    }
}
