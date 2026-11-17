package com.glowingfederal.moderncombatmovement.mixins;
import java.util.*;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Modern potion-duration scaling also covers custom NBT effects; foreign cloud producers retain their rules. */
@Mixin(value=EntityAreaEffectCloud.class,remap=false)
public abstract class LingeringCloudMixin {
 @ModifyArg(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Ljava/util/List;addAll(Ljava/util/Collection;)Z"),index=0)
 private Collection<PotionEffect> mcm$customScale(Collection<PotionEffect> effects){
  EntityAreaEffectCloud cloud=(EntityAreaEffectCloud)(Object)this;
  if(!cloud.getEntityData().getBoolean("MCMThrownPotion")||!com.glowingfederal.moderncombatmovement.config.Settings.kit(cloud.world.isRemote))return effects;
  List<PotionEffect> scaled=new ArrayList<PotionEffect>(effects.size());
  for(PotionEffect effect:effects){ResourceLocation id=effect.getPotion().getRegistryName();String owner=id==null?"":id.getNamespace();
   boolean known=owner.equals("minecraft")||owner.equals("moderncombatmovement")||owner.equals("etfuturum")&&com.glowingfederal.moderncombatmovement.compat.Content.uab;
   scaled.add(known&&!effect.getPotion().isInstant()?new PotionEffect(effect.getPotion(),Math.max(1,effect.getDuration()/4),effect.getAmplifier(),effect.getIsAmbient(),effect.doesShowParticles()):effect);
  }return scaled;
 }
}
