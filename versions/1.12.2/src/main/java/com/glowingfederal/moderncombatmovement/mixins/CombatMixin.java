package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class CombatMixin {
 @Inject(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At("HEAD"),cancellable=true) private void mcm$properties(net.minecraft.entity.Entity target,CallbackInfo ci) { EntityPlayer p=(EntityPlayer)(Object)this;if(com.glowingfederal.moderncombatmovement.combat.SpearMechanics.owned(p)){com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);ci.cancel();return;}CombatHooks.update(p); }

 @ModifyVariable(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At(value="STORE",ordinal=0),ordinal=0) private float mcm$damage(float damage) {
  EntityPlayer p=(EntityPlayer)(Object)this;
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

}
