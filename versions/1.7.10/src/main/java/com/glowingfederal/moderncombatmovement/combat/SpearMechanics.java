package com.glowingfederal.moderncombatmovement.combat;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks;
/** Server-owned 1.21.11 piercing/kinetic weapon actions. Packets never supply a target or damage. */
public final class SpearMechanics {
 private SpearMechanics(){}
 public static boolean owned(EntityPlayer p){return ModernEquipment.enabled(p)&&ModernEquipment.spear(CombatHooks.held(p))!=null;}
 public static boolean use(ItemStack stack,EntityPlayer p){
  if(!ModernEquipment.enabled(p)||ModernEquipment.spear(stack)==null)return false;
  PlayerState state=PlayerHooks.state(p);state.spearContacts.clear();state.kineticStack=stack;
  p.setItemInUse(stack,72000);return true;
 }
 public static void clearInactive(EntityPlayer p){
  if(!ModernEquipment.spearUsing(p)){PlayerState s=PlayerHooks.state(p);s.spearContacts.clear();s.kineticStack=null;}
 }
 public static void charge(ItemStack stack,EntityPlayer p,int remaining){
  SpearSpec spec=ModernEquipment.spear(stack);if(spec==null||p.worldObj.isRemote||!ModernEquipment.enabled(p))return;
  int elapsed=72000-remaining-spec.delay;if(elapsed<0)return;
  PlayerState state=PlayerHooks.state(p);if(state.kineticStack!=stack){state.spearContacts.clear();state.kineticStack=stack;}
  long now=p.worldObj.getTotalWorldTime();
  for(Iterator<Map.Entry<UUID,Long>> it=state.spearContacts.entrySet().iterator();it.hasNext();)if(now-it.next().getValue()>=10)it.remove();
  net.minecraft.util.Vec3 look=p.getLookVec();double speed=projection(p,look)*20;
  double base=p.getEntityAttribute(SharedMonsterAttributes.attackDamage).getBaseValue();
  for(Entity target:targets(p,look)){
   Long previous=state.spearContacts.get(target.getUniqueID());if(previous!=null&&now-previous<10)continue;
   state.spearContacts.put(target.getUniqueID(),now);
   double relative=Math.max(0,speed-projection(target,look)*20);
   boolean dismount=spec.dismount(elapsed,speed),knockback=spec.knockback(elapsed,speed),damage=spec.damage(elapsed,relative);
   if(dismount||knockback||damage)stab(p,stack,target,spec.chargeDamage(base,relative),damage,knockback,dismount);
  }
 }
 public static boolean jab(EntityPlayer p){
  if(p.isUsingItem()||!owned(p)||p.worldObj.isRemote||!p.isEntityAlive())return false;
  CombatHooks.update(p);PlayerState state=PlayerHooks.state(p);
  if(CombatMath.strength(state.attackTicks,0,CombatHooks.speed(p))<1)return false;
  ItemStack stack=CombatHooks.held(p);float base=CombatHooks.attackDamage(p);
  net.minecraft.util.Vec3 look=p.getLookVec();for(Entity target:targets(p,look))stab(p,stack,target,base,true,true,false);
  state.attackTicks=0;
  lunge(p,stack);p.swingItem();p.addExhaustion(.1F);return true;
 }
 private static void lunge(EntityPlayer p,ItemStack stack){
  int level=EquipmentEnchantments.level(EquipmentEnchantments.lunge,stack);
  if(level<=0||com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p)||p.ridingEntity!=null||p.isInWater()||!(p.getFoodStats().getFoodLevel()>6||p.capabilities.allowFlying))return;
  net.minecraft.util.Vec3 look=p.getLookVec();p.addVelocity(look.xCoord*.458F*level,0,look.zCoord*.458F*level);
  stack.damageItem(1,p);p.addExhaustion(4*level);sync(p);
 }
 private static boolean stab(EntityPlayer p,ItemStack stack,Entity target,float base,boolean hurt,boolean knockback,boolean dismount){
  if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.AttackEntityEvent(p,target)))return false;
  float enchanted=0;
  if(target instanceof EntityLivingBase){enchanted=EnchantmentHelper.func_152377_a(stack,((EntityLivingBase)target).getCreatureAttribute());int sharp=stack==null?0:EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.sharpness.effectId,stack);if(sharp>0)enchanted+=.5F+.5F*sharp-1.25F*sharp;}
  int fire=EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId,stack);boolean temporary=hurt&&fire>0&&!target.isBurning();if(temporary)target.setFire(1);
  double vx=target.motionX,vy=target.motionY,vz=target.motionZ;
  boolean damaged=hurt&&target.attackEntityFrom(new SpearDamageSource(p),Math.max(0,base+enchanted));
  if(!damaged&&temporary)target.extinguish();
  if(knockback){
   int bonus=target instanceof EntityLivingBase?EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId,stack):0;double yaw=p.rotationYaw*Math.PI/180;
   if(target instanceof EntityLivingBase){if(Settings.knockback(false))KnockbackHooks.apply((EntityLivingBase)target,p,.4F+bonus*.5F,Math.sin(yaw),-Math.cos(yaw));else((EntityLivingBase)target).knockBack(p,.4F+bonus*.5F,Math.sin(yaw),-Math.cos(yaw));}
   else target.addVelocity(-Math.sin(yaw)*(.4+bonus*.5),.1,Math.cos(yaw)*(.4+bonus*.5));
   p.motionX*=.6;p.motionZ*=.6;p.setSprinting(false);PlayerHooks.state(p).sprinting=false;
  }
  boolean dismounted=false;if(dismount&&target.ridingEntity!=null){target.mountEntity(null);dismounted=true;}
  if(damaged||knockback||dismounted){
   if(damaged&&target instanceof EntityLivingBase){EnchantmentHelper.func_151384_a((EntityLivingBase)target,p);for(Object entry:EnchantmentHelper.getEnchantments(stack).entrySet()){java.util.Map.Entry enchant=(java.util.Map.Entry)entry;Enchantment effect=Enchantment.enchantmentsList[(Integer)enchant.getKey()];if(effect!=null)effect.func_151368_a(p,target,(Integer)enchant.getValue());}}
   if(stack!=null&&target instanceof EntityLivingBase){stack.hitEntity((EntityLivingBase)target,p);if(stack.stackSize<=0){if(stack==com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p))com.glowingfederal.moderncombatmovement.offhand.Offhands.set(p,null);else p.destroyCurrentEquippedItem();}}
   if(damaged&&fire>0)target.setFire(fire*4);
   if(target instanceof EntityPlayerMP&&target.velocityChanged){sync(target);target.velocityChanged=false;target.motionX=vx;target.motionY=vy;target.motionZ=vz;}
   p.addExhaustion(.1F);return true;
  }
  return false;
 }
 public static List<Entity> targets(EntityPlayer p,net.minecraft.util.Vec3 look){
  net.minecraft.util.AxisAlignedBB own=box(p);net.minecraft.util.Vec3 eye=vec(p.posX,own.minY+PlayerHooks.state(p).pose.eyeHeight,p.posZ);
  double extra=Math.max(0,projection(p,look)),max=(p.capabilities.isCreativeMode?6.5:4.5)+extra;
  net.minecraft.util.Vec3 start=vec(eye.xCoord+look.xCoord*2,eye.yCoord+look.yCoord*2,eye.zCoord+look.zCoord*2),end=vec(eye.xCoord+look.xCoord*max,eye.yCoord+look.yCoord*max,eye.zCoord+look.zCoord*max);
  net.minecraft.util.MovingObjectPosition wall=ray(p,eye,end);if(wall!=null){end=wall.hitVec;if(distance(eye,end)<2)return Collections.emptyList();}
  net.minecraft.util.AxisAlignedBB search=bounds(Math.min(start.xCoord,end.xCoord)-1.0625,Math.min(start.yCoord,end.yCoord)-1.0625,Math.min(start.zCoord,end.zCoord)-1.0625,Math.max(start.xCoord,end.xCoord)+1.0625,Math.max(start.yCoord,end.yCoord)+1.0625,Math.max(start.zCoord,end.zCoord)+1.0625);
  List<Entity> result=new ArrayList<Entity>();
  for(Object o:p.worldObj.getEntitiesWithinAABBExcludingEntity(p,search)){
   Entity e=(Entity)o;if(e.isDead||!e.canBeCollidedWith()||sameVehicle(p,e)||e instanceof EntityPlayer&&!p.canAttackPlayer((EntityPlayer)e))continue;
   net.minecraft.util.AxisAlignedBB actual=box(e);net.minecraft.util.MovingObjectPosition intercept=actual.calculateIntercept(start,end);
   if(inside(actual,start)||intercept!=null){result.add(e);continue;}
   net.minecraft.util.MovingObjectPosition margin=actual.expand(.125,.125,.125).calculateIntercept(start,end);if(margin==null)continue;
   net.minecraft.util.Vec3 center=vec((actual.minX+actual.maxX)/2,(actual.minY+actual.maxY)/2,(actual.minZ+actual.maxZ)/2);net.minecraft.util.MovingObjectPosition obstruction=ray(p,margin.hitVec,center);
   if(obstruction!=null)center=obstruction.hitVec;
   if(actual.calculateIntercept(margin.hitVec,center)!=null)result.add(e);
  }
  return result;
 }
 private static boolean sameVehicle(Entity p,Entity target){Entity a=p,b=target;while(a.ridingEntity!=null)a=a.ridingEntity;while(b.ridingEntity!=null)b=b.ridingEntity;return a==b;}
 private static double projection(Entity e,net.minecraft.util.Vec3 look){
  if(!(e instanceof EntityPlayer)){while(e.ridingEntity!=null)e=e.ridingEntity;}
  if(e instanceof MotionAccess){MotionState m=((MotionAccess)e).mcm$motion();return m.x*look.xCoord+m.y*look.yCoord+m.z*look.zCoord;}
  return(e.posX-e.prevPosX)*look.xCoord+(e.posY-e.prevPosY)*look.yCoord+(e.posZ-e.prevPosZ)*look.zCoord;
 }
 private static net.minecraft.util.MovingObjectPosition ray(EntityPlayer p,net.minecraft.util.Vec3 start,net.minecraft.util.Vec3 end){return p.worldObj.func_147447_a(vec(start.xCoord,start.yCoord,start.zCoord),vec(end.xCoord,end.yCoord,end.zCoord),false,true,false);}
 private static net.minecraft.util.AxisAlignedBB box(Entity e){return e.boundingBox;}
 private static net.minecraft.util.AxisAlignedBB bounds(double x1,double y1,double z1,double x2,double y2,double z2){return net.minecraft.util.AxisAlignedBB.getBoundingBox(x1,y1,z1,x2,y2,z2);}
 private static net.minecraft.util.Vec3 vec(double x,double y,double z){return net.minecraft.util.Vec3.createVectorHelper(x,y,z);}
 private static double distance(net.minecraft.util.Vec3 a,net.minecraft.util.Vec3 b){double x=a.xCoord-b.xCoord,y=a.yCoord-b.yCoord,z=a.zCoord-b.zCoord;return Math.sqrt(x*x+y*y+z*z);}
 private static boolean inside(net.minecraft.util.AxisAlignedBB b,net.minecraft.util.Vec3 p){return p.xCoord>b.minX&&p.xCoord<b.maxX&&p.yCoord>b.minY&&p.yCoord<b.maxY&&p.zCoord>b.minZ&&p.zCoord<b.maxZ;}
 public static void sync(Entity e){e.velocityChanged=true;if(e instanceof EntityPlayerMP)((EntityPlayerMP)e).playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S12PacketEntityVelocity(e));}
}
