package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.movement.*;
import com.glowingfederal.moderncombatmovement.render.WingAnimation;
import org.junit.Test;
import static org.junit.Assert.*;

public class FlightMathTest {
    @Test public void levelGlideFromRestUsesModernGravityAndLift() {
        FlightMotion v=new FlightMotion();
        FlightMath.travel(v,0,1,0,0,.08);
        assertEquals(0,v.x,0);assertEquals(-.01764,v.y,1e-8);assertEquals(.001782,v.z,1e-9);
    }
    @Test public void levelGlideRetainsHorizontalMomentum() {
        FlightMotion v=new FlightMotion();v.set(0,0,1);
        FlightMath.travel(v,0,1,0,0,.08);
        assertEquals(.991782,v.z,1e-7);assertEquals(-.01764,v.y,1e-8);
    }
    @Test public void pitchUpTradesHorizontalMomentumForHeight() {
        FlightMotion v=new FlightMotion();v.set(0,-.2,1);
        FlightMath.travel(v,0,Math.cos(Math.PI/6),(float)(-Math.PI/6),-.5F,.08);
        assertEquals(.987883875,v.z,1e-7);assertEquals(-.1503075,v.y,1e-7);
    }
    @Test public void rocketBoostFollowsNativeRecurrence() {
        FlightMotion v=new FlightMotion();
        FlightMath.boost(v,0,0,1);assertEquals(.85,v.z,1e-12);
        FlightMath.boost(v,0,0,1);assertEquals(1.275,v.z,1e-12);
        for(int i=0;i<100;i++)FlightMath.boost(v,0,0,1);
        assertEquals(1.7,v.z,1e-12);
    }
    @Test public void verticalLookAndSlowGravityRemainFinite() {
        FlightMotion v=new FlightMotion();v.set(0,0,1);
        FlightMath.travel(v,0,0,(float)(-Math.PI/2),-1,.01);
        assertTrue(Double.isFinite(v.x)&&Double.isFinite(v.y)&&Double.isFinite(v.z));
        assertEquals(-.0098,v.y,1e-8);
    }
    @Test public void wingsAdvancePerTickAndInterpolatePerFrame() {
        WingAnimation wings=new WingAnimation();wings.tick(true,false,0,0,1);
        assertEquals((float)(Math.PI/9)*.3F,wings.x,1e-7);
        assertEquals(wings.x/2,wings.x(.5F),1e-7);
        float before=wings.x;for(int frame=0;frame<1000;frame++)wings.x(.75F);
        assertEquals(before,wings.x,0);
    }
}
