package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.movement.*;
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class LivingMixin implements com.glowingfederal.moderncombatmovement.combat.MotionAccess {
 @org.spongepowered.asm.mixin.Unique private final com.glowingfederal.moderncombatmovement.combat.MotionState mcm$motion=new com.glowingfederal.moderncombatmovement.combat.MotionState();
 public com.glowingfederal.moderncombatmovement.combat.MotionState mcm$motion() { return mcm$motion; }
 @Inject(method={"onEntityUpdate","func_70030_z"},at=@At("HEAD")) private void mcm$knownSpeed(CallbackInfo ci) { net.minecraft.entity.EntityLivingBase e=(net.minecraft.entity.EntityLivingBase)(Object)this;mcm$motion.sample(e.dimension,e.posX,e.posY,e.posZ); }
 @org.spongepowered.asm.mixin.Unique private net.minecraft.util.DamageSource mcm$source;
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD")) private void mcm$damageSource(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci) { mcm$source=source; }
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("RETURN")) private void mcm$damageDone(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci) { mcm$source=null; }

 @Inject(method={"onLivingUpdate","func_70636_d"},at=@At("HEAD")) private void mcm$radial(CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer p=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(p) && MovementMath.stopHorizontal(p.motionX,p.motionZ)) { p.motionX=0; p.motionZ=0; }
  }
 }
 @ModifyConstant(method={"onLivingUpdate","func_70636_d"},constant={@Constant(doubleValue=.005,ordinal=0),@Constant(doubleValue=.005,ordinal=2)}) private double mcm$horizontal(double value) {
  return (Object)this instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)(Object)this) ? 0 : value;
 }
 @ModifyConstant(method={"onLivingUpdate","func_70636_d"},constant=@Constant(doubleValue=.005,ordinal=1)) private double mcm$vertical(double value) {
  return (Object)this instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)(Object)this) ? .003 : value;
 }
 @Inject(method={"jump","func_70664_aZ"},at=@At("HEAD")) private void mcm$jumpBefore(CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer) PlayerHooks.state((EntityPlayer)(Object)this).jumpVelocity=((EntityLivingBase)(Object)this).motionY;
 }
 @Inject(method={"jump","func_70664_aZ"},at=@At("RETURN")) private void mcm$jumpAfter(CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer p=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(p)) p.motionY=Math.max(p.motionY,PlayerHooks.state(p).jumpVelocity);
  }
 }
 @Inject(method={"moveEntityWithHeading","func_70612_e"},at=@At("HEAD"),cancellable=true) private void mcm$water(float strafe,float forward,CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer && WaterTravel.travel((EntityPlayer)(Object)this,strafe,forward)) ci.cancel();
 }

 @Inject(method={"knockBack","func_70653_a"},at=@At("HEAD"),cancellable=true) private void mcm$knockback(net.minecraft.entity.Entity attacker,float strength,double x,double z,CallbackInfo ci) {
  EntityLivingBase p=(EntityLivingBase)(Object)this;
  if(((com.glowingfederal.moderncombatmovement.combat.ShieldBlockAccess)p).mcm$fullyBlocked()){ci.cancel();return;}
  if(mcm$source instanceof com.glowingfederal.moderncombatmovement.combat.SpearDamageSource||mcm$source instanceof com.glowingfederal.moderncombatmovement.combat.CombatBlastDamageSource){ci.cancel();return;}
  if(com.glowingfederal.moderncombatmovement.config.Settings.knockback(p.worldObj.isRemote) && com.glowingfederal.moderncombatmovement.combat.CombatHooks.managed(mcm$source)) {
   if(mcm$source instanceof com.glowingfederal.moderncombatmovement.compat.BoostRocketDamage){net.minecraft.entity.Entity projectile=mcm$source.getSourceOfDamage();x=projectile.posX-p.posX;z=projectile.posZ-p.posZ;}
   com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks.apply(p,attacker,.4F,x,z); ci.cancel();
  }
 }

 @ModifyArg(method={"moveEntityWithHeading","func_70612_e"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;moveFlying(FFF)V"),index=2,require=0) private float mcm$groundMcp(float acceleration) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer player=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(player) && player.onGround && !player.isInWater() && !player.isInLava()) return MovementMath.groundAcceleration(player.getAIMoveSpeed(),player.worldObj.getBlockState(new net.minecraft.util.BlockPos(player.posX,player.getEntityBoundingBox().minY-1,player.posZ)).getBlock().slipperiness);
  }
  return acceleration;
 }

 @ModifyArg(method={"moveEntityWithHeading","func_70612_e"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70060_a(FFF)V"),index=2,require=0) private float mcm$groundSrg(float acceleration) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer player=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(player) && player.onGround && !player.isInWater() && !player.isInLava()) return MovementMath.groundAcceleration(player.getAIMoveSpeed(),player.worldObj.getBlockState(new net.minecraft.util.BlockPos(player.posX,player.getEntityBoundingBox().minY-1,player.posZ)).getBlock().slipperiness);
  }
  return acceleration;
 }
 @Inject(method={"getArmSwingAnimationEnd","func_82166_i"},at=@At("HEAD"),cancellable=true)private void mcm$spearSwing(CallbackInfoReturnable<Integer> ci){
  if(!((Object)this instanceof EntityPlayer))return;EntityPlayer p=(EntityPlayer)(Object)this;if(!com.glowingfederal.moderncombatmovement.compat.ModernEquipment.enabled(p))return;
  com.glowingfederal.moderncombatmovement.combat.SpearSpec spec=com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(com.glowingfederal.moderncombatmovement.combat.CombatHooks.held(p));if(spec==null)return;
  int ticks=(int)(spec.swingSeconds*20);
  if(p.isPotionActive(net.minecraft.potion.Potion.digSpeed))ticks-=1+p.getActivePotionEffect(net.minecraft.potion.Potion.digSpeed).getAmplifier();
  else if(p.isPotionActive(net.minecraft.potion.Potion.digSlowdown))ticks+=(1+p.getActivePotionEffect(net.minecraft.potion.Potion.digSlowdown).getAmplifier())*2;
  ci.setReturnValue(ticks);
 }

}
