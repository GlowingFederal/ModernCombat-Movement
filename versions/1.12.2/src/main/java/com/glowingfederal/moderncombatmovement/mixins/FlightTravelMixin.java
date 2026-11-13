package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=EntityLivingBase.class,remap=false,priority=1200)
public abstract class FlightTravelMixin {
 @Inject(method={"travel","func_191986_a"},at=@At("HEAD"),cancellable=true)private void mcm$flight(float strafe,float vertical,float forward,CallbackInfo ci){if((Object)this instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightTravel.travel((EntityPlayer)(Object)this))ci.cancel();}
 @Inject(method={"updateElytra","func_184616_r"},at=@At("HEAD"),cancellable=true)private void mcm$modernGlideTick(CallbackInfo ci){if((Object)this instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.managed((EntityPlayer)(Object)this))ci.cancel();}
}
