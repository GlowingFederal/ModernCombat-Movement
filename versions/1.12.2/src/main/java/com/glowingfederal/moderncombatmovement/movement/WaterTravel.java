package com.glowingfederal.moderncombatmovement.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.material.Material;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class WaterTravel {
 private WaterTravel() {}
 /** Replaces only player water travel; native collision, block callbacks and the enclosing player travel statistics still execute. */
 public static boolean travel(EntityPlayer p,float strafe,float forward) {
  if(!PlayerHooks.active(p) || !Settings.swimming(p.world.isRemote) || !p.isInWater() || p.world.isRemote && !com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.local(p)) return false;
  PlayerState s=PlayerHooks.state(p);
  double beforeX=p.posX, beforeY=p.posY, beforeZ=p.posZ;
  if(s.swimming) {
   double look=p.getLookVec().y;
   if(look<=0 || s.jump || p.world.getBlockState(new net.minecraft.util.math.BlockPos(p.posX,p.getEntityBoundingBox().minY+.9,p.posZ)).getMaterial()==Material.WATER) p.motionY=MovementMath.swimmingY(p.motionY,look);
  }
  float drag=p.isSprinting() ? .9F : .8F, acceleration=.02F;
  float efficiency=Math.min(3,net.minecraft.enchantment.EnchantmentHelper.getDepthStriderModifier(p))/3F;
  if(!p.onGround) efficiency*=.5F;
  drag+=(.54600006F-drag)*efficiency;
  acceleration+=(p.getAIMoveSpeed()-acceleration)*efficiency;
  p.moveRelative(strafe,0,forward,acceleration);
  p.move(net.minecraft.entity.MoverType.SELF,p.motionX,p.motionY,p.motionZ);
  if(p.collidedHorizontally && p.isOnLadder()) p.motionY=.2;
  boolean falling=p.motionY<=0;
  p.motionX*=drag; p.motionY*=.8F; p.motionZ*=drag;
  p.motionY=MovementMath.fluidGravity(p.motionY,falling,.08);
  if(p.collidedHorizontally && p.isOffsetPositionInLiquid(p.motionX,p.motionY+.6F-p.posY+beforeY,p.motionZ)) p.motionY=.3F;
  p.prevLimbSwingAmount=p.limbSwingAmount;
  float limb=Math.min(1,(float)Math.sqrt((p.posX-beforeX)*(p.posX-beforeX)+(p.posZ-beforeZ)*(p.posZ-beforeZ))*4);
  p.limbSwingAmount+=(limb-p.limbSwingAmount)*.4F; p.limbSwing+=p.limbSwingAmount;
  return true;
 }
}
