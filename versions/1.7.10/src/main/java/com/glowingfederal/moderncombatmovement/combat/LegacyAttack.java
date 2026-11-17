package com.glowingfederal.moderncombatmovement.combat;

import java.util.Collection;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.enchantment.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import com.glowingfederal.moderncombatmovement.api.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks;
public final class LegacyAttack {
 private LegacyAttack() {}
 public static void attack(EntityPlayer p,Entity target,ItemProperties profile) {
  attack(p,target,profile,CombatHooks.held(p),false);
 }
 public static void spin(EntityPlayer p,Entity target,ItemStack stack){
  if(stack!=null)attack(p,target,CombatApi.find(CombatHooks.id(stack)),stack,true);
 }
 private static void attack(EntityPlayer p,Entity target,ItemProperties profile,ItemStack stack,boolean spin){
  if(profile==null)return;
  PlayerState state=PlayerHooks.state(p);
  if(p.worldObj.isRemote) { state.attackTicks=0; return; }
  if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.AttackEntityEvent(p,target))) return;
  if(stack!=null && stack.getItem().onLeftClickEntity(stack,p,target)) return;
  if(!target.canAttackWithItem() || target.hitByEntity(p)) return;
  CombatHooks.update(p);
  float strength=CombatMath.strength(state.attackTicks,.5F,CombatHooks.speed(p)); state.attackTicks=0;
  float base=spin?8:CombatHooks.attackDamage(p);
  float enchanted=target instanceof EntityLivingBase ? EnchantmentHelper.func_152377_a(stack,((EntityLivingBase)target).getCreatureAttribute()) : 0;
  if(com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)&&com.glowingfederal.moderncombatmovement.compat.Tridents.aquatic(target))enchanted+=2.5F*com.glowingfederal.moderncombatmovement.compat.Tridents.level(com.glowingfederal.moderncombatmovement.compat.Tridents.impaling,stack);
  int sharp=stack==null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId,stack);
  if(sharp>0) enchanted+=.5F+.5F*sharp-1.25F*sharp;
  boolean critical=CombatMath.critical(strength,p.fallDistance,p.onGround,p.isOnLadder(),p.isInWater(),p.isPotionActive(Potion.blindness),p.ridingEntity!=null,p.isSprinting(),target instanceof EntityLivingBase);
  float damage=CombatMath.damage(Math.max(0,base),enchanted,strength,critical)+MaceMechanics.bonus(p)*(critical?1.5F:1);
  boolean sprint=strength>.9F && p.isSprinting();
  int enchantKnockback=stack==null?0:EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId,stack);
  int fire=stack==null?0:EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId,stack);
  boolean temporaryFire=fire>0 && target instanceof EntityLivingBase && !target.isBurning(); if(temporaryFire) target.setFire(1);
  double vx=target.motionX,vy=target.motionY,vz=target.motionZ;
  if(!target.attackEntityFrom(DamageSource.causePlayerDamage(p),damage)) { if(temporaryFire) target.extinguish();com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,"entity.player.attack.nodamage",1,1); return; }
  float power=(enchantKnockback+(sprint ? 1 : 0))*.5F;
  double yaw=p.rotationYaw*Math.PI/180;
  if(power>0) {
   if(target instanceof EntityLivingBase) KnockbackHooks.apply((EntityLivingBase)target,p,power,Math.sin(yaw),-Math.cos(yaw));
   else target.addVelocity(-Math.sin(yaw)*power,.1,Math.cos(yaw)*power);
   p.motionX*=.6; p.motionZ*=.6; p.setSprinting(false); state.sprinting=false;
  }
  boolean swept=false;
  if(profile.sweeping && strength>.9F && !critical && !sprint && p.onGround && p.distanceWalkedModified-p.prevDistanceWalkedModified<p.getAIMoveSpeed()) {
   swept=true;com.glowingfederal.moderncombatmovement.network.Packets.effect(p.worldObj,2,p.posX-Math.sin(yaw),p.boundingBox.minY+p.height*.5,p.posZ+Math.cos(yaw),1);
   for(Object o:p.worldObj.getEntitiesWithinAABB(EntityLivingBase.class,target.boundingBox.expand(1,.25,1))) {
    EntityLivingBase nearby=(EntityLivingBase)o;
    if(nearby!=p && nearby!=target && !p.isOnSameTeam(nearby) && p.getDistanceSqToEntity(nearby)<9) {
     KnockbackHooks.apply(nearby,p,.4F,Math.sin(yaw),-Math.cos(yaw)); int sweep=EquipmentEnchantments.level(EquipmentEnchantments.sweeping,stack);nearby.attackEntityFrom(DamageSource.causePlayerDamage(p),1+damage*sweep/(sweep+1F));
    }
   }
  }
  if(target instanceof EntityPlayerMP && target.velocityChanged) {
   ((EntityPlayerMP)target).playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S12PacketEntityVelocity(target));
   target.velocityChanged=false; target.motionX=vx; target.motionY=vy; target.motionZ=vz;
  }
  if(sprint)com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,"entity.player.attack.knockback",1,1);
  com.glowingfederal.moderncombatmovement.compat.ItemSounds.play(p,critical?"entity.player.attack.crit":swept?"entity.player.attack.sweep":strength>.9F?"entity.player.attack.strong":"entity.player.attack.weak",1,1);
  if(critical) p.onCriticalHit(target); if(enchanted>0) p.onEnchantmentCritical(target);
  p.setLastAttacker(target);
  if(target instanceof EntityLivingBase) EnchantmentHelper.func_151384_a((EntityLivingBase)target,p);
  EnchantmentHelper.func_151385_b(p,target);
  EntityLivingBase victim=target instanceof EntityLivingBase ? (EntityLivingBase)target : null;
  if(target instanceof net.minecraft.entity.boss.EntityDragonPart && ((net.minecraft.entity.boss.EntityDragonPart)target).entityDragonObj instanceof EntityLivingBase) victim=(EntityLivingBase)((net.minecraft.entity.boss.EntityDragonPart)target).entityDragonObj;
  if(stack!=null && victim!=null) { stack.hitEntity(victim,p); if(stack.stackSize<=0){if(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p)==stack)com.glowingfederal.moderncombatmovement.offhand.Offhands.set(p,null);else if(CombatHooks.held(p)==stack)p.destroyCurrentEquippedItem();} }
  if(target instanceof EntityLivingBase) { if(fire>0) target.setFire(fire*4); p.addStat(net.minecraft.stats.StatList.damageDealtStat,Math.round(damage*10)); }
  p.addExhaustion(.1F);
 }
}
