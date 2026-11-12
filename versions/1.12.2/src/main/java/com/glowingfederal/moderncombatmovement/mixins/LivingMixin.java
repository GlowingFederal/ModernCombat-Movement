package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.movement.*;
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class LivingMixin {
 @org.spongepowered.asm.mixin.Unique private net.minecraft.util.DamageSource mcm$source;
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD")) private void mcm$damageSource(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci) { mcm$source=source; }
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("RETURN")) private void mcm$damageDone(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci) { mcm$source=null; }

 @Inject(method={"onLivingUpdate","func_70636_d"},at=@At("HEAD")) private void mcm$radial(CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer p=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(p) && MovementMath.stopHorizontal(p.motionX,p.motionZ)) { p.motionX=0; p.motionZ=0; }
  }
 }
 @ModifyConstant(method={"onLivingUpdate","func_70636_d"},constant={@Constant(doubleValue=.003,ordinal=0),@Constant(doubleValue=.003,ordinal=2)}) private double mcm$horizontal(double value) {
  return (Object)this instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)(Object)this) ? 0 : value;
 }
 @ModifyConstant(method={"onLivingUpdate","func_70636_d"},constant=@Constant(doubleValue=.003,ordinal=1)) private double mcm$vertical(double value) {
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
 @Inject(method={"travel","func_191986_a"},at=@At("HEAD"),cancellable=true) private void mcm$water(float strafe,float vertical,float forward,CallbackInfo ci) {
  if((Object)this instanceof EntityPlayer && WaterTravel.travel((EntityPlayer)(Object)this,strafe,forward)) ci.cancel();
 }

 @Inject(method={"knockBack","func_70653_a"},at=@At("HEAD"),cancellable=true) private void mcm$knockback(net.minecraft.entity.Entity attacker,float strength,double x,double z,CallbackInfo ci) {
  EntityLivingBase p=(EntityLivingBase)(Object)this;
  if(com.glowingfederal.moderncombatmovement.config.Settings.knockback(p.world.isRemote) && com.glowingfederal.moderncombatmovement.combat.CombatHooks.managed(mcm$source)) {
   com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks.apply(p,attacker,strength,x,z); ci.cancel();
  }
 }

 @ModifyArg(method={"travel","func_191986_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;moveRelative(FFFF)V"),index=3,require=0) private float mcm$groundMcp(float acceleration) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer player=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(player) && player.onGround && !player.isInWater() && !player.isInLava()) return MovementMath.groundAcceleration(player.getAIMoveSpeed(),player.world.getBlockState(new net.minecraft.util.math.BlockPos(player.posX,player.getEntityBoundingBox().minY-1,player.posZ)).getBlock().slipperiness);
  }
  return acceleration;
 }

 @ModifyArg(method={"travel","func_191986_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_191958_b(FFFF)V"),index=3,require=0) private float mcm$groundSrg(float acceleration) {
  if((Object)this instanceof EntityPlayer) { EntityPlayer player=(EntityPlayer)(Object)this;
   if(PlayerHooks.active(player) && player.onGround && !player.isInWater() && !player.isInLava()) return MovementMath.groundAcceleration(player.getAIMoveSpeed(),player.world.getBlockState(new net.minecraft.util.math.BlockPos(player.posX,player.getEntityBoundingBox().minY-1,player.posZ)).getBlock().slipperiness);
  }
  return acceleration;
 }
}
