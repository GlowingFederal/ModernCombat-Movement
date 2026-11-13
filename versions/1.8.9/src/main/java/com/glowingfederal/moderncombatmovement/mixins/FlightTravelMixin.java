package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=EntityLivingBase.class,remap=false,priority=1200)
public abstract class FlightTravelMixin {
 @Inject(method={"moveEntityWithHeading","func_70612_e"},at=@At("HEAD"),cancellable=true)private void mcm$flight(float strafe,float forward,CallbackInfo ci){if((Object)this instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightTravel.travel((EntityPlayer)(Object)this))ci.cancel();}
 
}
