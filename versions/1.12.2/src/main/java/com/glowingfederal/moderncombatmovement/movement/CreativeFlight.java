package com.glowingfederal.moderncombatmovement.movement;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Temporary server-owned creative boost, relative to the pre-existing capability speed. */
public final class CreativeFlight {
 private CreativeFlight(){}
 public static void tick(EntityPlayer p){
  if(p.world.isRemote)return;PlayerState s=PlayerHooks.state(p);
  boolean allowed=s.flightBoostLease>0&&!s.flightBoostBlocked&&p.capabilities.isCreativeMode&&p.capabilities.allowFlying&&p.capabilities.isFlying&&p.isEntityAlive()&&!p.isPlayerSleeping()&&!p.isRiding();
  if(s.flightBoostLease>0)s.flightBoostLease--;
  float multiplier=allowed?Math.min(s.requestedFlightBoost,com.glowingfederal.moderncombatmovement.config.Settings.creativeFlightLimit):1;
  if(!(multiplier>1)){restore(p,true);return;}
  float current=p.capabilities.getFlySpeed();
  if(!Float.isNaN(s.baselineFlySpeed)&&current!=s.appliedFlySpeed){s.baselineFlySpeed=Float.NaN;s.flightBoostBlocked=true;s.flightBoostLease=0;return;}
  if(!Float.isFinite(current)||current<0){s.flightBoostBlocked=true;s.flightBoostLease=0;return;}
  if(Float.isNaN(s.baselineFlySpeed))s.baselineFlySpeed=current;
  float target=s.baselineFlySpeed*multiplier;
  if(current!=target){((FlightSpeedAccess)p.capabilities).mcm$flightSpeed(target);s.appliedFlySpeed=target;sync(p);}
 }
 public static void restore(EntityPlayer p,boolean notify){
  PlayerState s=PlayerHooks.state(p);if(s==null)return;
  if(!Float.isNaN(s.baselineFlySpeed)&&p.capabilities.getFlySpeed()==s.appliedFlySpeed){((FlightSpeedAccess)p.capabilities).mcm$flightSpeed(s.baselineFlySpeed);if(notify&&p instanceof net.minecraft.entity.player.EntityPlayerMP)sync(p);}
  s.baselineFlySpeed=Float.NaN;s.requestedFlightBoost=1;s.flightBoostLease=0;
 }
 public static void clone(EntityPlayer original,EntityPlayer copy){
  PlayerState s=PlayerHooks.state(original);
  if(!Float.isNaN(s.baselineFlySpeed)&&copy.capabilities.getFlySpeed()==s.appliedFlySpeed)((FlightSpeedAccess)copy.capabilities).mcm$flightSpeed(s.baselineFlySpeed);
  restore(original,false);
 }
 public static void save(EntityPlayer p,net.minecraft.nbt.NBTTagCompound data){
  PlayerState s=PlayerHooks.state(p);
  if(!Float.isNaN(s.baselineFlySpeed)&&p.capabilities.getFlySpeed()==s.appliedFlySpeed)data.getCompoundTag("abilities").setFloat("flySpeed",s.baselineFlySpeed);
 }
 private static void sync(EntityPlayer p){if(p instanceof net.minecraft.entity.player.EntityPlayerMP&&((net.minecraft.entity.player.EntityPlayerMP)p).connection!=null)p.sendPlayerAbilities();}

}
