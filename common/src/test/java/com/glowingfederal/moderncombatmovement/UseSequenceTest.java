package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.pose.UseSequence;
import org.junit.Test;
import static org.junit.Assert.*;

public class UseSequenceTest {
    @Test public void releaseRejectsAnOlderActiveSnapshot() {
        int raise = UseSequence.next(0), release = UseSequence.next(raise);
        assertFalse(UseSequence.acknowledges(raise, release));
        assertTrue(UseSequence.acknowledges(release, release));
        assertFalse(UseSequence.newer(raise, release));
        assertFalse(UseSequence.newer(release, release));
    }

    @Test public void signedAndUnsignedBoundariesDoNotLatchUse() {
        int next = UseSequence.next(Integer.MAX_VALUE);
        assertTrue(UseSequence.newer(next, Integer.MAX_VALUE));
        assertTrue(UseSequence.acknowledges(next, next));
        assertEquals(1, UseSequence.next(-1));
        assertTrue(UseSequence.newer(1, -1));
    }
}
