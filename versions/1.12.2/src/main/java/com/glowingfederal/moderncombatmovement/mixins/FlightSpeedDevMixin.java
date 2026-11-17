package com.glowingfederal.moderncombatmovement.mixins;
/** The legacy native setter is client-only. Access the same capability field safely on both sides. */
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.entity.player.PlayerCapabilities.class,remap=false)
public abstract class FlightSpeedDevMixin implements com.glowingfederal.moderncombatmovement.movement.FlightSpeedAccess {
 @org.spongepowered.asm.mixin.Shadow private float flySpeed;
 public void mcm$flightSpeed(float value){flySpeed=value;}
}
