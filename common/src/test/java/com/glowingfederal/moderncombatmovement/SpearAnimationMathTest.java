package com.glowingfederal.moderncombatmovement;
import com.glowingfederal.moderncombatmovement.combat.SpearSpec;
import com.glowingfederal.moderncombatmovement.render.SpearAnimationMath;
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
}
