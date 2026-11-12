package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class CombatMixin {
 @Inject(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At("HEAD")) private void mcm$properties(net.minecraft.entity.Entity target,CallbackInfo ci) { CombatHooks.update((EntityPlayer)(Object)this); }

 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="STORE",ordinal=0),ordinal=0) private float mcm$damage(float damage) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  com.glowingfederal.moderncombatmovement.api.ItemProperties profile=CombatHooks.profile(p);
  if(profile==null || !Settings.combat(p.world.isRemote)) return damage;
  double intrinsic=1;
  for(net.minecraft.entity.ai.attributes.AttributeModifier modifier : p.getHeldItemMainhand().getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot.MAINHAND).get("generic.attackDamage")) if(modifier.getOperation()==0) intrinsic+=modifier.getAmount();
  return Math.max(0,damage+(float)(profile.attackDamage-intrinsic));
 }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;knockBack(Lnet/minecraft/entity/Entity;FDD)V"),require=0) private void mcm$extraMcp(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) { mcm$extra(target,attacker,power,x,z); }
 @Redirect(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70653_a(Lnet/minecraft/entity/Entity;FDD)V"),require=0) private void mcm$extraSrg(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) { mcm$extra(target,attacker,power,x,z); }
 private void mcm$extra(net.minecraft.entity.EntityLivingBase target,net.minecraft.entity.Entity attacker,float power,double x,double z) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(CombatHooks.profile(p)!=null && Settings.knockback(p.world.isRemote)) com.glowingfederal.moderncombatmovement.knockback.KnockbackHooks.apply(target,attacker,power,x,z);
  else target.knockBack(attacker,power,x,z);
 }
}
