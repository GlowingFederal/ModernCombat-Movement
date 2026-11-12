package com.glowingfederal.moderncombatmovement.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.material.Material;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class WaterTravel {
 private WaterTravel() {}
 /** Replaces only player water travel; native collision, block callbacks and the enclosing player travel statistics still execute. */
 public static boolean travel(EntityPlayer p,float strafe,float forward) {
  if(!PlayerHooks.active(p) || !Settings.swimming(p.worldObj.isRemote) || !p.isInWater() || p.worldObj.isRemote && !com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.local(p)) return false;
  PlayerState s=PlayerHooks.state(p);
  double beforeX=p.posX, beforeY=p.posY, beforeZ=p.posZ;
  if(s.swimming) {
   double look=p.getLookVec().yCoord;
   if(look<=0 || s.jump || p.worldObj.getBlock((int)Math.floor(p.posX),(int)Math.floor(p.boundingBox.minY+.9),(int)Math.floor(p.posZ)).getMaterial()==Material.water) p.motionY=MovementMath.swimmingY(p.motionY,look);
  }
  float drag=p.isSprinting() ? .9F : .8F, acceleration=.02F;
  float efficiency=0;
  if(!p.onGround) efficiency*=.5F;
  drag+=(.54600006F-drag)*efficiency;
  acceleration+=(p.getAIMoveSpeed()-acceleration)*efficiency;
  p.moveFlying(strafe,forward,acceleration);
  p.moveEntity(p.motionX,p.motionY,p.motionZ);
  if(p.isCollidedHorizontally && p.isOnLadder()) p.motionY=.2;
  boolean falling=p.motionY<=0;
  p.motionX*=drag; p.motionY*=.8F; p.motionZ*=drag;
  p.motionY=MovementMath.fluidGravity(p.motionY,falling,.08);
  if(p.isCollidedHorizontally && p.isOffsetPositionInLiquid(p.motionX,p.motionY+.6F-p.posY+beforeY,p.motionZ)) p.motionY=.3F;
  p.prevLimbSwingAmount=p.limbSwingAmount;
  float limb=Math.min(1,(float)Math.sqrt((p.posX-beforeX)*(p.posX-beforeX)+(p.posZ-beforeZ)*(p.posZ-beforeZ))*4);
  p.limbSwingAmount+=(limb-p.limbSwingAmount)*.4F; p.limbSwing+=p.limbSwingAmount;
  return true;
 }
}
