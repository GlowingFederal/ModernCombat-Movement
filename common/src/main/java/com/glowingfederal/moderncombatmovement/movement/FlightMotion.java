package com.glowingfederal.moderncombatmovement.movement;

/** Reused scratch velocity; each adapter reads the entity's actual motion before applying a rule. */
public final class FlightMotion {
    public double x,y,z;
    public void set(double x,double y,double z) { this.x=x;this.y=y;this.z=z; }
}
