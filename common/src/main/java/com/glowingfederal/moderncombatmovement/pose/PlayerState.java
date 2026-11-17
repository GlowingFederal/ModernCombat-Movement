package com.glowingfederal.moderncombatmovement.pose;

public final class PlayerState {
 public boolean ownedStepHeight;
    public Pose pose = Pose.STANDING;
    public boolean swimming, jump, ownedDimensions;
    public float eye = 1.62F, previousEye = 1.62F, swimAmount, previousSwimAmount;
    public int lastSent = -1, attackTicks;
    public int shieldCooldown,windCooldown,pearlCooldown;
    public int lastShieldCooldown,lastWindCooldown,lastPearlCooldown;
    public int offhandIntentSerial,offhandAckSerial;
    /** Transient Riptide state. The original hand stack owns the contact attack. */
    public int spinTicks;
    public int kineticHitTicks=10;
    public Object spinWeapon, spinBox;
    public boolean spinAttacking, spinOffhand;
    public Object lastWeapon;
    public double jumpVelocity;
    public boolean sprinting, wasForward;
    public int sprintWindow;
    /** Server-thread kinetic contact cooldowns; expired entries are removed while the item is used. */
    public final java.util.Map<java.util.UUID,Long> spearContacts = new java.util.HashMap<java.util.UUID,Long>();
    public Object kineticStack;
    public double maceImpactY=Double.NaN;
    public boolean flightOwned,flightJump;
    public int flightTicks;
    public float requestedFlightBoost=1, baselineFlySpeed=Float.NaN, appliedFlySpeed;
    public int flightBoostLease;
    public boolean flightBoostBlocked;
    public final com.glowingfederal.moderncombatmovement.movement.FlightMotion flightMotion=new com.glowingfederal.moderncombatmovement.movement.FlightMotion();
    public final com.glowingfederal.moderncombatmovement.render.WingAnimation wings=new com.glowingfederal.moderncombatmovement.render.WingAnimation();
    public void animate() {
        previousEye = eye;
        eye += (pose.eyeHeight - eye) * 0.5F;
        previousSwimAmount = swimAmount;
        swimAmount = Math.max(0, Math.min(1, swimAmount + (pose == Pose.LOW ? 0.09F : -0.09F)));
    }
    public float cameraEye(float partial) { return previousEye + (eye - previousEye) * partial; }
    public float swim(float partial) { return previousSwimAmount + (swimAmount - previousSwimAmount) * partial; }
}
