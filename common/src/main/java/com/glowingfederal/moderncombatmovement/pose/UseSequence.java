package com.glowingfederal.moderncombatmovement.pose;

/** Ordering for hand-use intents and acknowledgements, including serial rollover. Zero is reserved for internal calls. */
public final class UseSequence {
    private UseSequence() {}

    public static int next(int previous) {
        int next = previous + 1;
        return next == 0 ? 1 : next;
    }

    public static boolean newer(int candidate, int accepted) {
        return candidate - accepted > 0;
    }

    public static boolean acknowledges(int acknowledged, int pending) {
        return acknowledged - pending >= 0;
    }
}
