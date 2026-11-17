package com.glowingfederal.moderncombatmovement.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.material.Material;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class WaterTravel {
 private WaterTravel() {}
 /** Replaces only player water travel; native collision, block callbacks and the enclosing player travel statistics still execute. */
 public static boolean travel(EntityPlayer p,float strafe,float forward) {
  if(PlayerHooks.active(p)&&!p.isInWater()&&lavaHeight(p)>0&&(!p.world.isRemote||com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.local(p)))return lava(p,strafe,forward);
  if(!PlayerHooks.active(p) || !Settings.swimming(p.world.isRemote) || !p.isInWater() || p.world.isRemote && !com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.local(p)) return false;
  PlayerState s=PlayerHooks.state(p);
  double beforeX=p.posX, beforeY=p.posY, beforeZ=p.posZ;
  if(s.swimming) {
   double look=p.getLookVec().y;
   if(look<=0 || s.jump || p.world.getBlockState(new net.minecraft.util.math.BlockPos(p.posX,p.getEntityBoundingBox().minY+.9,p.posZ)).getMaterial()==Material.WATER) p.motionY=MovementMath.swimmingY(p.motionY,look);
  }
  boolean falling=p.motionY<=0;
  double effectiveGravity=com.glowingfederal.moderncombatmovement.compat.KitEffects.gravity(p,.08);
  float drag=p.isSprinting() ? .9F : .8F, acceleration=.02F;
  float efficiency=Math.min(3,net.minecraft.enchantment.EnchantmentHelper.getDepthStriderModifier(p))/3F;
  if(!p.onGround) efficiency*=.5F;
  drag+=(.54600006F-drag)*efficiency;
  acceleration+=(p.getAIMoveSpeed()-acceleration)*efficiency;
  p.moveRelative(strafe,0,forward,acceleration);
  p.move(net.minecraft.entity.MoverType.SELF,p.motionX,p.motionY,p.motionZ);
  if(p.collidedHorizontally && p.isOnLadder()) p.motionY=.2;
  p.motionX*=drag; p.motionY*=.8F; p.motionZ*=drag;
  p.motionY=MovementMath.fluidGravity(p.motionY,falling,effectiveGravity);
  if(p.collidedHorizontally && p.isOffsetPositionInLiquid(p.motionX,p.motionY+.6F-p.posY+beforeY,p.motionZ)) p.motionY=.3F;
  p.prevLimbSwingAmount=p.limbSwingAmount;
  float limb=Math.min(1,(float)Math.sqrt((p.posX-beforeX)*(p.posX-beforeX)+(p.posZ-beforeZ)*(p.posZ-beforeZ))*4);
  p.limbSwingAmount+=(limb-p.limbSwingAmount)*.4F; p.limbSwing+=p.limbSwingAmount;
  return true;
 }

 private static boolean lava(EntityPlayer p,float strafe,float forward){
  double beforeX=p.posX,beforeY=p.posY,beforeZ=p.posZ;
  boolean falling=p.motionY<=0;
  double gravity=com.glowingfederal.moderncombatmovement.compat.KitEffects.gravity(p,.08);
  p.moveRelative(strafe,0,forward,.02F);
  p.move(net.minecraft.entity.MoverType.SELF,p.motionX,p.motionY,p.motionZ);
  p.motionX*=.5;p.motionZ*=.5;
  if(lavaHeight(p)<=.4){p.motionY*=.8F;p.motionY=MovementMath.fluidGravity(p.motionY,falling,gravity);}else p.motionY*=.5;
  p.motionY-=gravity/4;
  if(p.collidedHorizontally&&p.isOffsetPositionInLiquid(p.motionX,p.motionY+.6F-p.posY+beforeY,p.motionZ))p.motionY=.3F;
  p.prevLimbSwingAmount=p.limbSwingAmount;
  float limb=Math.min(1,(float)Math.sqrt((p.posX-beforeX)*(p.posX-beforeX)+(p.posZ-beforeZ)*(p.posZ-beforeZ))*4);
  p.limbSwingAmount+=(limb-p.limbSwingAmount)*.4F;p.limbSwing+=p.limbSwingAmount;
  return true;
 }
 private static double lavaHeight(EntityPlayer p){
  net.minecraft.util.math.AxisAlignedBB b=p.getEntityBoundingBox();double top=b.minY;
  for(int x=(int)Math.floor(b.minX+1e-7);x<(int)Math.ceil(b.maxX-1e-7);x++)for(int y=(int)Math.floor(b.minY);y<(int)Math.ceil(b.maxY);y++)for(int z=(int)Math.floor(b.minZ+1e-7);z<(int)Math.ceil(b.maxZ-1e-7);z++){
   net.minecraft.block.state.IBlockState block=p.world.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z));
   if(block.getMaterial()==Material.LAVA)top=Math.max(top,y+1-net.minecraft.block.BlockLiquid.getLiquidHeightPercent(block.getValue(net.minecraft.block.BlockLiquid.LEVEL)));
  }
  return Math.max(0,top-b.minY);
 }
}
