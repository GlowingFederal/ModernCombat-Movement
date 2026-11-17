package com.glowingfederal.moderncombatmovement.mixins;
/** The legacy native setter is client-only. Access the same capability field safely on both sides. */
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.entity.player.PlayerCapabilities.class,remap=false)
public abstract class FlightSpeedMixin implements com.glowingfederal.moderncombatmovement.movement.FlightSpeedAccess {
 @org.spongepowered.asm.mixin.Shadow private float field_75096_f;
 public void mcm$flightSpeed(float value){field_75096_f=value;}
}
