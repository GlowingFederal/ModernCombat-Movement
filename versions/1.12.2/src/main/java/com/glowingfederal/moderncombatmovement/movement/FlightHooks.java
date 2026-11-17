package com.glowingfederal.moderncombatmovement.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import com.glowingfederal.moderncombatmovement.compat.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Canonical modern gliding lifecycle. Unknown equipment and external glider providers retain their owner. */
public final class FlightHooks {
 private FlightHooks(){}
 private static Potion levitation;
 public static boolean uabFlight;
 public static void init(){
  levitation=net.minecraft.init.MobEffects.LEVITATION;
  
 }
 public static ItemStack chest(EntityPlayer p){return p.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.CHEST);}
 public static boolean known(ItemStack s){if(s==null||s.isEmpty())return false;String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(s);return id.equals("minecraft:elytra")||id.equals("etfuturum:elytra")||id.equals("moderncombatmovement:elytra");}
 public static boolean usable(ItemStack s){return known(s)&&s.getItemDamage()<s.getMaxDamage()-1;}
 public static boolean managed(EntityPlayer p){return Settings.flight(p.world.isRemote)&&known(chest(p));}
 public static boolean raw(EntityPlayer p){return ((FlightAccess)p).mcm$glidingFlag();}
 public static boolean flying(EntityPlayer p){return raw(p)&&canGlide(p)&&(managed(p)||PlayerHooks.state(p).flightOwned);}
 public static boolean foreignFlying(EntityPlayer p){return raw(p)&&!managed(p)&&!PlayerHooks.state(p).flightOwned;}
 private static boolean canGlide(EntityPlayer p){return p.isEntityAlive()&&!p.onGround&&!p.isInWater()&&!p.isOnLadder()&&!(p.isRiding())&&!p.capabilities.isFlying&&!p.isPlayerSleeping()&&!p.isSpectator()&&(levitation==null||!p.isPotionActive(levitation))&&usable(chest(p));}
 public static boolean start(EntityPlayer p){
  if(!managed(p)||raw(p)||!canGlide(p)||p.isInWater()||p.isOnLadder())return false;
  ((FlightAccess)p).mcm$glidingFlag(true);PlayerHooks.state(p).flightOwned=true;return true;
 }
 public static void stop(EntityPlayer p){((FlightAccess)p).mcm$glidingFlag(true);((FlightAccess)p).mcm$glidingFlag(false);PlayerState s=PlayerHooks.state(p);s.flightOwned=false;s.flightTicks=0;}
 public static void tick(EntityPlayer p){
  PlayerState s=PlayerHooks.state(p);boolean client=p.world.isRemote;
  if(!managed(p)&&!s.flightOwned){s.flightTicks=0;return;}
  if(!Settings.flight(client)){if(s.flightOwned)stop(p);return;}
  if(raw(p)){s.flightOwned=true;
   if(!canGlide(p)){stop(p);return;}
   if(p.motionY>-.5&&p.fallDistance>1)p.fallDistance=1;
   if(!client&&(s.flightTicks+1)%20==0)chest(p).damageItem(1,p);
   if(s.flightTicks<Integer.MAX_VALUE)s.flightTicks++;
  }else{s.flightOwned=false;s.flightTicks=0;}

 }
 // Legacy LivingEntity.getLook uses head yaw; modern flight uses entity yaw.
 public static net.minecraft.util.math.Vec3d look(EntityPlayer p){float pitch=p.rotationPitch*(float)(Math.PI/180),yaw=-p.rotationYaw*(float)(Math.PI/180);float c=net.minecraft.util.math.MathHelper.cos(pitch);return new net.minecraft.util.math.Vec3d(net.minecraft.util.math.MathHelper.sin(yaw)*c,-net.minecraft.util.math.MathHelper.sin(pitch),net.minecraft.util.math.MathHelper.cos(yaw)*c);}
 public static double gravity(EntityPlayer p){return KitEffects.gravity(p,.08);}
}
