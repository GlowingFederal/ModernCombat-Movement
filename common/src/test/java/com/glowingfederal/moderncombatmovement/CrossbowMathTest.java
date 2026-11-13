package com.glowingfederal.moderncombatmovement;

import org.junit.Test;
import static org.junit.Assert.*;
import com.glowingfederal.moderncombatmovement.combat.CrossbowMath;

public class CrossbowMathTest {
    @Test public void chargeTimes() {
        assertEquals(25, CrossbowMath.chargeTicks(0));
        assertEquals(10, CrossbowMath.chargeTicks(3));
    }
    @Test public void pitchedSpreadRemainsTenDegrees() {
        double[] center=CrossbowMath.direction(35,-80,0), side=CrossbowMath.direction(35,-80,10);
        double dot=center[0]*side[0]+center[1]*side[1]+center[2]*side[2];
        assertEquals(Math.cos(Math.toRadians(10)),dot,1e-12);
        assertEquals(1, side[0]*side[0]+side[1]*side[1]+side[2]*side[2],1e-12);
    }
}
