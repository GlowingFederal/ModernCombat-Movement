/* UAB-derived rocket lifecycle helpers, corrected against the selected modern firework source; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public final class RocketEffects {
 private RocketEffects(){}
 public static boolean stars(ItemStack stack){return stack!=null&&stack.hasTagCompound()&&stack.getTagCompound().getCompoundTag("Fireworks").getTagList("Explosions",10).tagCount()>0;}
 /** Called before movement, so only the segment actually attempted this tick is tested. */
 public static boolean hits(Entity rocket,Entity ignored,double dx,double dy,double dz,boolean stars){
  Vec3 start=new Vec3(rocket.posX,rocket.posY,rocket.posZ),end=new Vec3(rocket.posX+dx,rocket.posY+dy,rocket.posZ+dz);
  MovingObjectPosition obstruction=rocket.worldObj.rayTraceBlocks(start,end,false,true,false);if(obstruction!=null)end=obstruction.hitVec;
  for(Object object:rocket.worldObj.getEntitiesWithinAABBExcludingEntity(rocket,rocket.getEntityBoundingBox().addCoord(dx,dy,dz).expand(1,1,1))){
   Entity target=(Entity)object;if(target==ignored||target.isDead||!target.canBeCollidedWith())continue;
   net.minecraft.util.AxisAlignedBB bounds=target.getEntityBoundingBox().expand(.3,.3,.3);
   if(bounds.isVecInside(start)||bounds.calculateIntercept(start,end)!=null)return true;
  }
  return stars&&obstruction!=null;
 }
 public static void damage(Entity rocket,Entity owner,EntityLivingBase attached,ItemStack stack){
  if(rocket.worldObj.isRemote||!stars(stack))return;
  int stars=stack.getTagCompound().getCompoundTag("Fireworks").getTagList("Explosions",10).tagCount();float damage=5+stars*2;BoostRocketDamage source=new BoostRocketDamage(rocket,owner);
  if(attached!=null)attached.attackEntityFrom(source,damage);
  for(Object object:rocket.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,rocket.getEntityBoundingBox().expand(5,5,5))){
   EntityLivingBase target=(EntityLivingBase)object;double distance=rocket.getDistanceToEntity(target);if(target==attached||distance>5)continue;boolean visible=false;
   for(int i=0;i<2;i++)if(rocket.worldObj.rayTraceBlocks(new Vec3(rocket.posX,rocket.posY,rocket.posZ),new Vec3(target.posX,target.getEntityBoundingBox().minY+target.height*.5*i,target.posZ),false,true,false)==null){visible=true;break;}
   if(visible)target.attackEntityFrom(source,damage*(float)Math.sqrt((5-distance)/5));
  }
 }
}
