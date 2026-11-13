package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
/** Optional UAB-injected methods are merged before this injection phase. No UAB class is linked. */
@Mixin(value=EntityPlayer.class,remap=false,priority=900)
public abstract class UabFlightBridgeMixin {
 @Inject(method="tickElytra",at=@At("HEAD"),cancellable=true,require=0)private void mcm$oneTick(CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)||PlayerHooks.state(p).flightOwned)ci.cancel();}
 @Inject(method="etfu$isElytraFlying",at=@At("HEAD"),cancellable=true,require=0)private void mcm$flag(CallbackInfoReturnable<Boolean> ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)||PlayerHooks.state(p).flightOwned)ci.setReturnValue(FlightHooks.raw(p));}
 @Inject(method="etfu$setElytraFlying",at=@At("HEAD"),cancellable=true,require=0)private void mcm$validate(boolean flag,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)||PlayerHooks.state(p).flightOwned){if(flag)FlightHooks.start(p);else FlightHooks.stop(p);ci.cancel();}}
 @Inject(method="etfu$getTicksElytraFlying",at=@At("HEAD"),cancellable=true,require=0)private void mcm$ticks(CallbackInfoReturnable<Float> ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p))ci.setReturnValue((float)PlayerHooks.state(p).flightTicks);}
}
