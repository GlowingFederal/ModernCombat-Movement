package com.glowingfederal.moderncombatmovement;
import com.glowingfederal.moderncombatmovement.combat.SpearSpec;
import com.glowingfederal.moderncombatmovement.render.SpearAnimationMath;
import com.glowingfederal.moderncombatmovement.render.CrossbowHandTransforms;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpearAnimationMathTest {
    @Test public void raisesAtTheNativeMaterialDelay() {
        for(SpearSpec spec:SpearSpec.values()) {
            assertEquals(0,new SpearAnimationMath.Use(spec,0).raise,0);
            assertEquals(1,new SpearAnimationMath.Use(spec,spec.delay).raise,0);
            assertEquals(1,new SpearAnimationMath.Use(spec,spec.delay+spec.damageDuration).back,0);
        }
    }
    @Test public void everyNativePhaseRemainsFinite() {
        for(SpearSpec spec:SpearSpec.values()) for(int tick=0;tick<400;tick++) {
            SpearAnimationMath.Use use=new SpearAnimationMath.Use(spec,tick);
            assertTrue(Float.isFinite(use.slow));
            assertTrue(Float.isFinite(use.fast));
            assertTrue(Float.isFinite(use.lower));
        }
    }

    @Test public void activeUseDoesNotAcquireEquipOrAttackTransforms() {
        for (SpearSpec spec : SpearSpec.values()) for (int sign : new int[]{-1, 1}) {
            Recording baseline = new Recording(), interruptedSwing = new Recording();
            SpearAnimationMath.firstPerson(baseline, spec, sign, 0, 0, true, spec.delay, 10);
            SpearAnimationMath.firstPerson(interruptedSwing, spec, sign, 1, .6F, true, spec.delay, 10);
            assertEquals(baseline.operations, interruptedSwing.operations);
        }
    }

    @Test public void firstUseFrameAndIdleHaveDifferentAnimationOwnership() {
        Recording use = new Recording(), idle = new Recording();
        SpearAnimationMath.firstPerson(use, SpearSpec.IRON, 1, 1, .5F, true, 0, 10);
        SpearAnimationMath.firstPerson(idle, SpearSpec.IRON, 1, 1, .5F, false, 0, 10);
        assertEquals(-.52F, use.firstY, .00001);
        assertEquals(-1.12F, idle.firstY, .00001);
        assertNotEquals(use.operations, idle.operations);
    }

    @Test public void kineticHitRecoilHasAFiniteBoundedLifecycle() {
        assertEquals(0, SpearAnimationMath.hitFeedback(0), 0);
        assertEquals(.4F, SpearAnimationMath.hitFeedback(3), .00001F);
        assertEquals(0, SpearAnimationMath.hitFeedback(10), .00001F);
        for (int tick = 0; tick < 100; tick++) {
            float recoil = SpearAnimationMath.hitFeedback(tick / 4F);
            assertTrue(recoil >= 0 && recoil <= .4F);
        }
    }

    private static final class Recording implements CrossbowHandTransforms.Matrix {
        final java.util.List<String> operations = new java.util.ArrayList<String>();
        double firstY;
        public void translate(double x, double y, double z) {
            if (operations.isEmpty()) firstY = y;
            operations.add("t:" + x + ":" + y + ":" + z);
        }
        public void rotate(float degrees, float x, float y, float z) {
            operations.add("r:" + degrees + ":" + x + ":" + y + ":" + z);
        }
        public void scale(float x, float y, float z) {
            operations.add("s:" + x + ":" + y + ":" + z);
        }
    }
}
