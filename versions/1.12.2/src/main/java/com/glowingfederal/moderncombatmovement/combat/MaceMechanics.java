package com.glowingfederal.moderncombatmovement.combat;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.*;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
import com.glowingfederal.moderncombatmovement.pose.*;
/** MaceItem's successful-hit and impulse lifecycle, independent of UAB's implementation. */
public final class MaceMechanics {
 private MaceMechanics(){}
 public static boolean owns(EntityPlayer p){return ModernEquipment.enabled(p)&&ModernEquipment.mace(CombatHooks.held(p));}
 public static boolean canSmash(EntityPlayer p){return !com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p)&&p.fallDistance>1.5F&&!p.isElytraFlying();}
 public static float bonus(EntityPlayer p){return owns(p)&&canSmash(p)?(float)WeaponMath.smashBonus(p.fallDistance,EquipmentEnchantments.level(EquipmentEnchantments.density,CombatHooks.held(p))):0;}
 public static void impact(EntityPlayer p,EntityLivingBase victim){
  if(p.world.isRemote||!owns(p)||!canSmash(p))return;
  float fall=p.fallDistance;PlayerState state=PlayerHooks.state(p);
  if(Double.isNaN(state.maceImpactY)||state.maceImpactY>p.posY)state.maceImpactY=p.posY;
  p.motionY=.01F;
  for(Object object:p.world.getEntitiesWithinAABB(EntityLivingBase.class,victim.getEntityBoundingBox().grow(3.5))){
   EntityLivingBase e=(EntityLivingBase)object;
   if(e==p||e==victim||p.isOnSameTeam(e)||e instanceof EntityPlayer&&((EntityPlayer)e).capabilities.isCreativeMode&&((EntityPlayer)e).capabilities.isFlying||e instanceof EntityPlayer&&((EntityPlayer)e).isSpectator()
      ||e instanceof EntityTameable&&((EntityTameable)e).isTamed()&&((EntityTameable)e).getOwner()==victim)continue;
   double x=e.posX-victim.posX,y=e.posY-victim.posY,z=e.posZ-victim.posZ,distance=Math.sqrt(x*x+y*y+z*z);
   double power=WeaponMath.smashKnockback(distance,fall,e.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue());
   if(power>0&&distance>0){e.addVelocity(x/distance*power,.7F,z/distance*power);SpearMechanics.sync(e);}
  }
  int wind=EquipmentEnchantments.level(EquipmentEnchantments.windBurst,CombatHooks.held(p));
  if(wind>0)CombatBlast.wind(p.world,p,p.posX,p.posY,p.posZ,3.5F,(float)WeaponMath.windBurstMultiplier(wind));
  if(victim.onGround)com.glowingfederal.moderncombatmovement.network.Packets.effect(p.world,1,victim.posX,victim.posY,victim.posZ,3.5F);
  p.fallDistance=0;SpearMechanics.sync(p);
  com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,victim.onGround?(fall>5?"item.mace.smash_ground_heavy":"item.mace.smash_ground"):"item.mace.smash_air",1,1);
 }
 public static void init(){net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());}
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.HIGHEST)public void fall(LivingFallEvent event){
   if(!(event.getEntityLiving() instanceof EntityPlayer))return;EntityPlayer p=(EntityPlayer)event.getEntityLiving();if(p.world.isRemote)return;
   PlayerState state=PlayerHooks.state(p);if(Double.isNaN(state.maceImpactY))return;
   float allowed=Math.max(0,Math.min(event.getDistance(),(float)(state.maceImpactY-p.posY)));event.setDistance(allowed);state.maceImpactY=Double.NaN;
  }
  @SubscribeEvent public void tick(LivingEvent.LivingUpdateEvent event){
   if(!(event.getEntityLiving() instanceof EntityPlayer))return;EntityPlayer p=(EntityPlayer)event.getEntityLiving();
   if(p.onGround||p.isInWater()||p.isOnLadder()||p.isRiding()||p.isElytraFlying()||p.capabilities.isFlying)PlayerHooks.state(p).maceImpactY=Double.NaN;
  }
 }
}
