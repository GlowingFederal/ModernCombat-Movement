package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.movement.*;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Native flag 7 is authoritative and already synchronized by vanilla entity tracking. */
@Mixin(value=EntityPlayer.class,remap=false,priority=1200)
public abstract class FlightMixin extends EntityLivingBase implements FlightAccess {
 protected FlightMixin(World w){super(w);}
 public boolean mcm$glidingFlag(){return getFlag(7);}
 public void mcm$glidingFlag(boolean value){setFlag(7,value);}
 @Inject(method={"getEyeHeight","func_70047_e"},at=@At("HEAD"),cancellable=true)private void mcm$flightEye(CallbackInfoReturnable<Float> ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)&&FlightHooks.flying(p))ci.setReturnValue(p.getDefaultEyeHeight()+.4F-1.62F);}
 @Inject(method={"writeEntityToNBT","func_70014_b"},at=@At("RETURN"))private void mcm$writeGlide(NBTTagCompound tag,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)||PlayerHooks.state(p).flightOwned)tag.setBoolean("FallFlying",FlightHooks.flying(p));}
 @Inject(method={"readEntityFromNBT","func_70037_a"},at=@At("RETURN"))private void mcm$readGlide(NBTTagCompound tag,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;if(FlightHooks.managed(p)&&tag.getBoolean("FallFlying")){mcm$glidingFlag(true);PlayerHooks.state(p).flightOwned=true;}}
}
