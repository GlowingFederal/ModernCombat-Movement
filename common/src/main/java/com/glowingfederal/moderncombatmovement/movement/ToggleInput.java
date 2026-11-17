package com.glowingfederal.moderncombatmovement.movement;

/** Physical key edges are sampled once; GUI/focus/lifecycle interruption requires a fresh toggle press. */
public final class ToggleInput {
    private boolean previous, latched;

    public boolean update(boolean down, boolean toggle, boolean valid) {
        if (!valid) {
            previous = down;
            latched = false;
            return false;
        }
        if (!toggle) {
            latched = false;
            previous = down;
            return down;
        }
        if (down && !previous) latched = !latched;
        previous = down;
        return latched;
    }

    public void reset(boolean down) {
        previous = down;
        latched = false;
    }
}
