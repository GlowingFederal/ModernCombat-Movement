package com.glowingfederal.moderncombatmovement.combat;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.enchantment.EnchantmentHelper;
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
 public static boolean use(ItemStack stack,EntityPlayer p,net.minecraft.util.EnumHand hand){
  if(!ModernEquipment.enabled(p)||ModernEquipment.spear(stack)==null)return false;
  PlayerState state=PlayerHooks.state(p);state.spearContacts.clear();state.kineticStack=stack;
  p.setActiveHand(hand);com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,ModernEquipment.spear(stack)==SpearSpec.WOODEN?"item.spear_wood.use":"item.spear.use",1,1);return true;
 }
 public static void clearInactive(EntityPlayer p){
  if(!ModernEquipment.spearUsing(p)){PlayerState s=PlayerHooks.state(p);s.spearContacts.clear();s.kineticStack=null;}
 }
 public static void charge(ItemStack stack,EntityPlayer p,int remaining){
  SpearSpec spec=ModernEquipment.spear(stack);if(spec==null||p.world.isRemote||!ModernEquipment.enabled(p))return;
  int elapsed=72000-remaining-spec.delay;if(elapsed<0)return;
  PlayerState state=PlayerHooks.state(p);if(state.kineticStack!=stack){state.spearContacts.clear();state.kineticStack=stack;}
  long now=p.world.getTotalWorldTime();
  for(Iterator<Map.Entry<UUID,Long>> it=state.spearContacts.entrySet().iterator();it.hasNext();)if(now-it.next().getValue()>=10)it.remove();
  net.minecraft.util.math.Vec3d look=p.getLookVec();double speed=projection(p,look)*20;
  double base=p.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
  boolean contact=false;
  for(Entity target:targets(p,look)){
   Long previous=state.spearContacts.get(target.getUniqueID());if(previous!=null&&now-previous<10)continue;
   state.spearContacts.put(target.getUniqueID(),now);
   double relative=Math.max(0,speed-projection(target,look)*20);
   boolean dismount=spec.dismount(elapsed,speed),knockback=spec.knockback(elapsed,speed),damage=spec.damage(elapsed,relative);
   if(dismount||knockback||damage)contact|=stab(p,stack,target,spec.chargeDamage(base,relative),damage,knockback,dismount);
  }
  if(contact){state.kineticHitTicks=0;state.lastSent=-1;com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,spec==SpearSpec.WOODEN?"item.spear_wood.hit":"item.spear.hit",1,1);}
 }
 public static boolean jab(EntityPlayer p){
  if(!owned(p)||p.world.isRemote||!p.isEntityAlive()||p.isSpectator())return false;
  CombatHooks.update(p);PlayerState state=PlayerHooks.state(p);
  if(CombatMath.strength(state.attackTicks,0,CombatHooks.speed(p))<1)return false;
  ItemStack stack=CombatHooks.held(p);float base=CombatHooks.attackDamage(p);
  net.minecraft.util.math.Vec3d look=p.getLookVec();for(Entity target:targets(p,look))stab(p,stack,target,base,true,true,false);
  state.attackTicks=0;p.resetCooldown();
  com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,ModernEquipment.spear(stack)==SpearSpec.WOODEN?"item.spear_wood.attack":"item.spear.attack",1,1);
  lunge(p,stack);p.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);p.addExhaustion(.1F);return true;
 }
 private static void lunge(EntityPlayer p,ItemStack stack){
  int level=EquipmentEnchantments.level(EquipmentEnchantments.lunge,stack);
  if(level<=0||com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p)||p.isRiding()||p.isInWater()||p.isElytraFlying()||!(p.getFoodStats().getFoodLevel()>6||p.capabilities.allowFlying))return;
  net.minecraft.util.math.Vec3d look=p.getLookVec();p.addVelocity(look.x*.458F*level,0,look.z*.458F*level);
  stack.damageItem(1,p);p.addExhaustion(4*level);sync(p);com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,"item.spear.lunge_"+Math.min(3,level),1,1);
 }
 private static boolean stab(EntityPlayer p,ItemStack stack,Entity target,float base,boolean hurt,boolean knockback,boolean dismount){
  if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.AttackEntityEvent(p,target)))return false;
  float enchanted=0;
  if(target instanceof EntityLivingBase){enchanted=EnchantmentHelper.getModifierForCreature(stack,((EntityLivingBase)target).getCreatureAttribute());}
  int fire=EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FIRE_ASPECT,stack);boolean temporary=hurt&&fire>0&&!target.isBurning();if(temporary)target.setFire(1);
  double vx=target.motionX,vy=target.motionY,vz=target.motionZ;
  boolean damaged=hurt&&target.attackEntityFrom(new SpearDamageSource(p),Math.max(0,base+enchanted));
  if(!damaged&&temporary)target.extinguish();
  if(knockback){
   int bonus=target instanceof EntityLivingBase?EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.KNOCKBACK,stack):0;double yaw=p.rotationYaw*Math.PI/180;
   if(target instanceof EntityLivingBase){if(Settings.knockback(false))KnockbackHooks.apply((EntityLivingBase)target,p,.4F+bonus*.5F,Math.sin(yaw),-Math.cos(yaw));else((EntityLivingBase)target).knockBack(p,.4F+bonus*.5F,Math.sin(yaw),-Math.cos(yaw));}
   else target.addVelocity(-Math.sin(yaw)*(.4+bonus*.5),.1,Math.cos(yaw)*(.4+bonus*.5));
   p.motionX*=.6;p.motionZ*=.6;p.setSprinting(false);PlayerHooks.state(p).sprinting=false;
  }
  boolean dismounted=false;if(dismount&&target.isRiding()){target.dismountRidingEntity();dismounted=true;}
  if(damaged||knockback||dismounted){
   if(damaged&&target instanceof EntityLivingBase){EnchantmentHelper.applyThornEnchantments((EntityLivingBase)target,p);for(java.util.Map.Entry<net.minecraft.enchantment.Enchantment,Integer> effect:EnchantmentHelper.getEnchantments(stack).entrySet())effect.getKey().onEntityDamaged(p,target,effect.getValue());}
   if(!stack.isEmpty()&&target instanceof EntityLivingBase){stack.hitEntity((EntityLivingBase)target,p);}
   if(damaged&&fire>0)target.setFire(fire*4);
   if(target instanceof EntityPlayerMP&&target.velocityChanged){sync(target);target.velocityChanged=false;target.motionX=vx;target.motionY=vy;target.motionZ=vz;}
   p.addExhaustion(.1F);return true;
  }
  return false;
 }
 public static List<Entity> targets(EntityPlayer p,net.minecraft.util.math.Vec3d look){
  net.minecraft.util.math.AxisAlignedBB own=box(p);net.minecraft.util.math.Vec3d eye=vec(p.posX,own.minY+PlayerHooks.state(p).pose.eyeHeight,p.posZ);
  double extra=Math.max(0,projection(p,look)),max=(p.capabilities.isCreativeMode?6.5:4.5)+extra;
  net.minecraft.util.math.Vec3d start=vec(eye.x+look.x*2,eye.y+look.y*2,eye.z+look.z*2),end=vec(eye.x+look.x*max,eye.y+look.y*max,eye.z+look.z*max);
  net.minecraft.util.math.RayTraceResult wall=ray(p,eye,end);if(wall!=null){end=wall.hitVec;if(distance(eye,end)<2)return Collections.emptyList();}
  net.minecraft.util.math.AxisAlignedBB search=bounds(Math.min(start.x,end.x)-1.0625,Math.min(start.y,end.y)-1.0625,Math.min(start.z,end.z)-1.0625,Math.max(start.x,end.x)+1.0625,Math.max(start.y,end.y)+1.0625,Math.max(start.z,end.z)+1.0625);
  List<Entity> result=new ArrayList<Entity>();
  for(Object o:p.world.getEntitiesWithinAABBExcludingEntity(p,search)){
   Entity e=(Entity)o;if(e.isDead||!e.canBeCollidedWith()||sameVehicle(p,e)||e instanceof EntityPlayer&&!p.canAttackPlayer((EntityPlayer)e))continue;
   net.minecraft.util.math.AxisAlignedBB actual=box(e);net.minecraft.util.math.RayTraceResult intercept=actual.calculateIntercept(start,end);
   if(inside(actual,start)||intercept!=null){result.add(e);continue;}
   net.minecraft.util.math.RayTraceResult margin=actual.grow(.125,.125,.125).calculateIntercept(start,end);if(margin==null)continue;
   net.minecraft.util.math.Vec3d center=vec((actual.minX+actual.maxX)/2,(actual.minY+actual.maxY)/2,(actual.minZ+actual.maxZ)/2);net.minecraft.util.math.RayTraceResult obstruction=ray(p,margin.hitVec,center);
   if(obstruction!=null)center=obstruction.hitVec;
   if(actual.calculateIntercept(margin.hitVec,center)!=null)result.add(e);
  }
  return result;
 }
 private static boolean sameVehicle(Entity p,Entity target){Entity a=p,b=target;while(a.isRiding())a=a.getRidingEntity();while(b.isRiding())b=b.getRidingEntity();return a==b;}
 private static double projection(Entity e,net.minecraft.util.math.Vec3d look){
  if(!(e instanceof EntityPlayer)){while(e.isRiding())e=e.getRidingEntity();}
  if(e instanceof MotionAccess){MotionState m=((MotionAccess)e).mcm$motion();return m.x*look.x+m.y*look.y+m.z*look.z;}
  return(e.posX-e.prevPosX)*look.x+(e.posY-e.prevPosY)*look.y+(e.posZ-e.prevPosZ)*look.z;
 }
 private static net.minecraft.util.math.RayTraceResult ray(EntityPlayer p,net.minecraft.util.math.Vec3d start,net.minecraft.util.math.Vec3d end){return p.world.rayTraceBlocks(vec(start.x,start.y,start.z),vec(end.x,end.y,end.z),false,true,false);}
 private static net.minecraft.util.math.AxisAlignedBB box(Entity e){return e.getEntityBoundingBox();}
 private static net.minecraft.util.math.AxisAlignedBB bounds(double x1,double y1,double z1,double x2,double y2,double z2){return new net.minecraft.util.math.AxisAlignedBB(x1,y1,z1,x2,y2,z2);}
 private static net.minecraft.util.math.Vec3d vec(double x,double y,double z){return new net.minecraft.util.math.Vec3d(x,y,z);}
 private static double distance(net.minecraft.util.math.Vec3d a,net.minecraft.util.math.Vec3d b){double x=a.x-b.x,y=a.y-b.y,z=a.z-b.z;return Math.sqrt(x*x+y*y+z*z);}
 private static boolean inside(net.minecraft.util.math.AxisAlignedBB b,net.minecraft.util.math.Vec3d p){return p.x>b.minX&&p.x<b.maxX&&p.y>b.minY&&p.y<b.maxY&&p.z>b.minZ&&p.z<b.maxZ;}
 public static void sync(Entity e){e.velocityChanged=true;if(e instanceof EntityPlayerMP)((EntityPlayerMP)e).connection.sendPacket(new net.minecraft.network.play.server.SPacketEntityVelocity(e));}
}
