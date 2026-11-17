package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.movement.ToggleInput;
import org.junit.Test;
import static org.junit.Assert.*;

public class ToggleInputTest {
    @Test public void onePressTogglesOnceAndSecondPressStops() {
        ToggleInput input = new ToggleInput();
        assertTrue(input.update(true, true, true));
        for (int tick = 0; tick < 40; tick++) assertTrue(input.update(true, true, true));
        assertTrue(input.update(false, true, true));
        assertFalse(input.update(true, true, true));
    }

    @Test public void focusLossDoesNotRelatchAHeldKey() {
        ToggleInput input = new ToggleInput();
        assertTrue(input.update(true, true, true));
        assertFalse(input.update(true, true, false));
        assertFalse(input.update(true, true, true));
        assertFalse(input.update(false, true, true));
        assertTrue(input.update(true, true, true));
    }

    @Test public void holdModeAndModeChangesPreservePhysicalRelease() {
        ToggleInput input = new ToggleInput();
        assertTrue(input.update(true, false, true));
        assertFalse(input.update(false, false, true));
        assertTrue(input.update(true, true, true));
        assertFalse(input.update(false, false, true));
    }
}
