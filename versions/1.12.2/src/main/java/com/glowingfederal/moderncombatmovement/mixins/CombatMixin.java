package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class CombatMixin {
 @Inject(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At("HEAD"),cancellable=true) private void mcm$properties(net.minecraft.entity.Entity target,CallbackInfo ci) { EntityPlayer p=(EntityPlayer)(Object)this;if(!com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinAttacking&&com.glowingfederal.moderncombatmovement.combat.SpearMechanics.owned(p)){com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);ci.cancel();return;}CombatHooks.update(p); }

 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="STORE",ordinal=0),ordinal=0) private float mcm$damage(float damage) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinAttacking)return 8;
  com.glowingfederal.moderncombatmovement.api.ItemProperties profile=CombatHooks.profile(p);
  if(profile==null || !Settings.combat(p.world.isRemote)) return damage;
  return CombatHooks.attackDamage(p);
 }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;knockBack(Lnet/minecraft/entity/Entity;FDD)V"),require=0) private void mcm$extraMcp(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) { mcm$extra(target,attacker,power,x,z); }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70653_a(Lnet/minecraft/entity/Entity;FDD)V"),require=0) private void mcm$extraSrg(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) { mcm$extra(target,attacker,power,x,z); }
 private void mcm$extra(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(CombatHooks.profile(p)!=null && Settings.knockback(p.world.isRemote)) com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks.apply(target,attacker,power,x,z);
  else target.knockBack(attacker,power,x,z);
 }
 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;resetCooldown()V"),ordinal=0,require=0)private float mcm$maceMcp(float damage){return damage+com.glowingfederal.moderncombatmovement.combat.MaceMechanics.bonus((EntityPlayer)(Object)this);}
 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;func_184821_cY()V"),ordinal=0,require=0)private float mcm$maceSrg(float damage){return damage+com.glowingfederal.moderncombatmovement.combat.MaceMechanics.bonus((EntityPlayer)(Object)this);}


 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;getHeldItemMainhand()Lnet/minecraft/item/ItemStack;"),require=0)
 private net.minecraft.item.ItemStack mcm$spinWeaponMcp(EntityPlayer p){return mcm$spinWeapon(p);}
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;func_184614_ca()Lnet/minecraft/item/ItemStack;"),require=0)
 private net.minecraft.item.ItemStack mcm$spinWeaponSrg(EntityPlayer p){return mcm$spinWeapon(p);}
 private net.minecraft.item.ItemStack mcm$spinWeapon(EntityPlayer p){com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);return s.spinAttacking?(net.minecraft.item.ItemStack)s.spinWeapon:p.getHeldItemMainhand();}
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraftforge/common/ForgeHooks;onPlayerAttackTarget(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)Z"))
 private boolean mcm$spinAttackEvent(EntityPlayer p,net.minecraft.entity.Entity target){
  if(!com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinAttacking)return net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(p,target);
  net.minecraft.item.ItemStack stack=mcm$spinWeapon(p);
  return !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.AttackEntityEvent(p,target))&&!stack.getItem().onLeftClickEntity(stack,p,target);
 }
 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="STORE",ordinal=0),ordinal=1)
 private float mcm$impaling(float bonus,net.minecraft.entity.Entity target){
  net.minecraft.item.ItemStack stack=mcm$spinWeapon((EntityPlayer)(Object)this);
  return com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)&&com.glowingfederal.moderncombatmovement.compat.Tridents.aquatic(target)?bonus+2.5F*com.glowingfederal.moderncombatmovement.compat.Tridents.level(com.glowingfederal.moderncombatmovement.compat.Tridents.impaling,stack):bonus;
 }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;setHeldItem(Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;)V"),require=0)
 private void mcm$brokenSpinMcp(EntityPlayer p,net.minecraft.util.EnumHand hand,net.minecraft.item.ItemStack stack){mcm$brokenSpin(p,hand,stack);}
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;func_184611_a(Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;)V"),require=0)
 private void mcm$brokenSpinSrg(EntityPlayer p,net.minecraft.util.EnumHand hand,net.minecraft.item.ItemStack stack){mcm$brokenSpin(p,hand,stack);}
 private void mcm$brokenSpin(EntityPlayer p,net.minecraft.util.EnumHand hand,net.minecraft.item.ItemStack stack){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  p.setHeldItem(s.spinAttacking&&s.spinOffhand?net.minecraft.util.EnumHand.OFF_HAND:hand,stack);
 }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraftforge/event/ForgeEventFactory;onPlayerDestroyItem(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumHand;)V"))
 private void mcm$spinDestroyEvent(EntityPlayer p,net.minecraft.item.ItemStack stack,net.minecraft.util.EnumHand hand){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(p,stack,s.spinAttacking&&s.spinOffhand?net.minecraft.util.EnumHand.OFF_HAND:hand);
 }

}
