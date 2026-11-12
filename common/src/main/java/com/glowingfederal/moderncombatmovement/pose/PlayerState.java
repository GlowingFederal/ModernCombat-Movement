package com.glowingfederal.moderncombatmovement.pose;

public final class PlayerState {
    public Pose pose = Pose.STANDING;
    public boolean swimming, jump, ownedDimensions;
    public float eye = 1.62F, previousEye = 1.62F, swimAmount, previousSwimAmount;
    public int lastSent = -1, attackTicks;
    public Object lastWeapon;
    public double jumpVelocity;
    public boolean sprinting, wasForward;
    public int sprintWindow;
    public void animate() {
        previousEye = eye;
        eye += (pose.eyeHeight - eye) * 0.5F;
        previousSwimAmount = swimAmount;
        swimAmount = Math.max(0, Math.min(1, swimAmount + (pose == Pose.LOW ? 0.09F : -0.09F)));
    }
    public float cameraEye(float partial) { return previousEye + (eye - previousEye) * partial; }
    public float swim(float partial) { return previousSwimAmount + (swimAmount - previousSwimAmount) * partial; }
}
