package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.render.SwimAnimationMath;
import org.junit.Test;
import static org.junit.Assert.*;

public class SwimAnimationMathTest {
    @Test public void nativeStrokeHasContinuousPhaseBoundaries() {
        for (float boundary : new float[]{14, 22, 26}) {
            float[] before = new float[5], after = new float[5];
            SwimAnimationMath.stroke(boundary - .0001F, before);
            SwimAnimationMath.stroke(boundary + .0001F, after);
            for (int index = 0; index < 3; index++) assertEquals(before[index], after[index], .001F);
        }
    }

    @Test public void idlePhaseIsStableAndEveryPoseIsFinite() {
        float[] first = new float[5], current = new float[5];
        SwimAnimationMath.stroke(7, first);
        for (int tick = 0; tick < 100; tick++) {
            SwimAnimationMath.stroke(7, current);
            assertArrayEquals(first, current, 0);
        }
        for (int phase = -100; phase < 100; phase++) {
            SwimAnimationMath.stroke(phase * .37F, current);
            for (float angle : current) assertTrue(Float.isFinite(angle));
        }
    }

    @Test public void angularBlendTakesTheShortPathAcrossWrap() {
        assertEquals((float)Math.PI, SwimAnimationMath.angle(3, -3, .5F), .00001F);
        assertEquals(1, SwimAnimationMath.angle(1, 2, 0), 0);
        assertEquals(2, SwimAnimationMath.angle(1, 2, 1), 0);
    }
}
