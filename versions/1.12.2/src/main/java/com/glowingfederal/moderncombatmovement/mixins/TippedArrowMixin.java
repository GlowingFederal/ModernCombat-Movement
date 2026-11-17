package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Native custom contents use the same modern duration scale as native base contents. */
@Mixin(value=EntityTippedArrow.class,remap=false)
public abstract class TippedArrowMixin {
 @Group(name="mcmCustomArrowEffect",min=1) @ModifyArg(method={"arrowHit","func_184548_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V",ordinal=1),index=0,require=0,expect=0)
 private PotionEffect mcm$customMcp(PotionEffect effect){return mcm$scale(effect);}
 @Group(name="mcmCustomArrowEffect",min=1) @ModifyArg(method={"arrowHit","func_184548_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70690_d(Lnet/minecraft/potion/PotionEffect;)V",ordinal=1),index=0,require=0,expect=0)
 private PotionEffect mcm$customSrg(PotionEffect effect){return mcm$scale(effect);}
 private PotionEffect mcm$scale(PotionEffect effect){
  EntityTippedArrow arrow=(EntityTippedArrow)(Object)this;
  if(arrow.getClass()!=EntityTippedArrow.class||!com.glowingfederal.moderncombatmovement.config.Settings.projectiles(arrow.world.isRemote))return effect;
  PotionEffect copy=new PotionEffect(effect.getPotion(),Math.max(1,effect.getDuration()/8),effect.getAmplifier(),effect.getIsAmbient(),effect.doesShowParticles());copy.setCurativeItems(effect.getCurativeItems());return copy;
 }
}
