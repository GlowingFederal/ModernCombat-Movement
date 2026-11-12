package com.glowingfederal.moderncombatmovement;
import org.junit.Test;
import static org.junit.Assert.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.movement.MovementMath;
import com.glowingfederal.moderncombatmovement.combat.CombatMath;
import com.glowingfederal.moderncombatmovement.knockback.KnockbackMath;
public class MechanicsTest {
 @Test public void poseDimensions() { assertEquals(1.5F,Pose.CROUCHING.height,0); assertEquals(1.27F,Pose.CROUCHING.eyeHeight,0); assertEquals(.4F,Pose.LOW.eyeHeight,0); }
 @Test public void obstructionFallback() { assertEquals(Pose.CROUCHING,PoseRules.resolve(Pose.LOW,Pose.STANDING,false,true,true)); assertEquals(Pose.LOW,PoseRules.resolve(Pose.STANDING,Pose.STANDING,false,false,true)); assertEquals(Pose.CROUCHING,PoseRules.resolve(Pose.CROUCHING,Pose.STANDING,true,true,false)); }
 @Test public void swimmingEntryAndExit() { assertFalse(PoseRules.swimming(false,true,true,false,false,false)); assertTrue(PoseRules.swimming(false,true,true,true,false,false)); assertTrue(PoseRules.swimming(true,true,true,false,false,false)); assertFalse(PoseRules.swimming(true,false,true,true,false,false)); assertFalse(PoseRules.swimming(true,true,true,true,false,true)); }
 @Test public void radialMotionThreshold() { assertFalse(MovementMath.stopHorizontal(.0022,.0022)); assertTrue(MovementMath.stopHorizontal(.002,.002)); assertFalse(MovementMath.stopHorizontal(.003,0)); }
 @Test public void fluidGravity() { assertEquals(-.003,MovementMath.fluidGravity(.01,true,.16),1e-12); assertEquals(.095,MovementMath.fluidGravity(.1,false,.08),1e-12); assertEquals(.1,MovementMath.fluidGravity(.1,false,0),0); }
 @Test public void swimmingVertical() { assertEquals(-.0425,MovementMath.swimmingY(0,-.5),1e-12); assertEquals(.03,MovementMath.swimmingY(0,.5),1e-12); }
 @Test public void groundAcceleration() { assertEquals(.1,MovementMath.groundAcceleration(.1F,.6F),2e-8); }
 @Test public void attackTiming() { assertEquals(.04F,CombatMath.strength(0,.5F,1.6),1e-7); assertEquals(1,CombatMath.strength(13,.5F,1.6),0); assertEquals(.4F,CombatMath.baseScale(.5F),0); assertEquals(13,CombatMath.damage(8,1,1,true),0); }
 @Test public void criticalSprintExclusion() { assertTrue(CombatMath.critical(1,1,false,false,false,false,false,false,true)); assertFalse(CombatMath.critical(1,1,false,false,false,false,false,true,true)); assertFalse(CombatMath.critical(.9F,1,false,false,false,false,false,false,true)); }
 @Test public void knockbackGroundAirResistance() { assertArrayEquals(new double[]{-.2,.4,0},KnockbackMath.apply(.4,.2,0,.4,0,1,0,true),1e-12); assertArrayEquals(new double[]{0,-.2,0},KnockbackMath.apply(.4,-.2,0,.4,.5,1,0,false),1e-12); assertArrayEquals(new double[]{.4,.2,0},KnockbackMath.apply(.4,.2,0,.4,1,1,0,true),0); }
 @Test public void cameraHalfStep() { PlayerState s=new PlayerState(); s.pose=Pose.LOW; s.animate(); assertEquals(1.01F,s.eye,1e-6); assertEquals(1.315F,s.cameraEye(.5F),1e-6); }
 @Test public void collisionAngle() { assertTrue(MovementMath.minorCollision(1,0,1,.01)); assertFalse(MovementMath.minorCollision(1,0,0,1)); }
}
