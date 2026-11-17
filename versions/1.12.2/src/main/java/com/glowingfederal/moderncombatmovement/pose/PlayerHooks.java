package com.glowingfederal.moderncombatmovement.pose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.network.Packets;
public final class PlayerHooks {
 private PlayerHooks() {}
 public static PlayerState state(EntityPlayer p) { return ((PlayerAccess)p).mcm$state(); }
 public static boolean active(EntityPlayer p) { return !com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(p) && Settings.movement(p.world.isRemote) && p.isEntityAlive() && !p.isPlayerSleeping() && !p.isSpectator() && !p.isRiding() && !p.capabilities.isFlying && !com.glowingfederal.moderncombatmovement.movement.FlightHooks.foreignFlying(p); }
 public static void before(EntityPlayer p) {
  com.glowingfederal.moderncombatmovement.compat.Tridents.beforeSpin(p);
  com.glowingfederal.moderncombatmovement.compat.WindCharges.tick(p);
  PlayerState s=state(p);
  com.glowingfederal.moderncombatmovement.combat.SpearMechanics.clearInactive(p);
  com.glowingfederal.moderncombatmovement.combat.CombatHooks.update(p);
  if(!p.world.isRemote) Packets.consume(p);
  com.glowingfederal.moderncombatmovement.movement.FlightHooks.tick(p);
  com.glowingfederal.moderncombatmovement.movement.CreativeFlight.tick(p);
  if(state(p).kineticHitTicks<10)state(p).kineticHitTicks++;
  if(!p.world.isRemote || ModernCombatMovement.proxy.local(p)) update(p);
  if(p.world.isRemote) {s.animate();s.wings.tick(com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p),s.pose==Pose.CROUCHING,p.motionX,p.motionY,p.motionZ);}
  if(s.attackTicks < Integer.MAX_VALUE) s.attackTicks++;
 }
 public static void after(EntityPlayer p) {
  com.glowingfederal.moderncombatmovement.compat.Tridents.afterSpin(p);
  if(active(p)||com.glowingfederal.moderncombatmovement.compat.Tridents.spinning(p)) dimensions(p); else release(p);
  if(!p.world.isRemote && p instanceof net.minecraft.entity.player.EntityPlayerMP) Packets.publish(p);
 }
 public static void update(EntityPlayer p) {
  PlayerState s=state(p);
  if(com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(p)){com.glowingfederal.moderncombatmovement.compat.MovementCompat.mirror(p);return;}
  if(com.glowingfederal.moderncombatmovement.compat.Tridents.spinning(p)&&p.isEntityAlive()){s.pose=Pose.SPIN_ATTACK;s.swimming=false;dimensions(p);return;}
  if(!active(p)) { s.pose=Pose.STANDING; s.swimming=false; release(p); return; }
  if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p)) {s.pose=Pose.FALL_FLYING;s.swimming=false;dimensions(p);return;}
  s.swimming=Settings.swimming(p.world.isRemote) && PoseRules.swimming(s.swimming,p.isSprinting(),p.isInWater(),p.isInsideOfMaterial(Material.WATER),p.isRiding(),p.capabilities.isFlying);
  Pose desired=s.swimming ? Pose.LOW : p.isSneaking() ? Pose.CROUCHING : Pose.STANDING;
  if(!Settings.crawling(p.world.isRemote) && !s.swimming) {
   if(fits(p,desired)) s.pose=desired;
  } else s.pose=PoseRules.resolve(s.pose,desired,fits(p,desired),fits(p,Pose.CROUCHING),fits(p,Pose.LOW));
  dimensions(p);
 }
 public static boolean fits(EntityPlayer p, Pose pose) {
  AxisAlignedBB b=p.getEntityBoundingBox();
  AxisAlignedBB candidate=new AxisAlignedBB(p.posX-.3+1e-7,b.minY+1e-7,p.posZ-.3+1e-7,p.posX+.3-1e-7,b.minY+pose.height-1e-7,p.posZ+.3-1e-7);
  return p.world.getCollisionBoxes(p,candidate).isEmpty();
 }
 public static void dimensions(EntityPlayer p) {
  PlayerState s=state(p); AxisAlignedBB b=p.getEntityBoundingBox();
  float h=s.pose.height;
  if(p.height==h && p.width==.6F) return;
  p.width=.6F; p.height=h;
  p.setEntityBoundingBox(new AxisAlignedBB(p.posX-.3,b.minY,p.posZ-.3,p.posX+.3,b.minY+h,p.posZ+.3));
  s.ownedDimensions=true;
 }
 public static void release(EntityPlayer p) {
  PlayerState s=state(p);
  if(com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(p)){s.ownedDimensions=false;return;}
  if(!s.ownedDimensions) return;
  if(!p.isEntityAlive() || com.glowingfederal.moderncombatmovement.movement.FlightHooks.foreignFlying(p) || p.isPlayerSleeping() || p.isElytraFlying()) { s.ownedDimensions=false; return; }
  if(fits(p,Pose.STANDING)) { s.pose=Pose.STANDING; dimensions(p); s.ownedDimensions=false; }
 }
}
