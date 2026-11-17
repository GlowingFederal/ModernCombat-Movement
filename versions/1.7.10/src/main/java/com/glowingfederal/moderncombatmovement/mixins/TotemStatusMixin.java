package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Native 1.12 entity-status presentation, confined to the legacy client. */
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class TotemStatusMixin {
 @Inject(method={"handleHealthUpdate","func_70103_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$totem(byte status,CallbackInfo ci){
  if(status==35){com.glowingfederal.moderncombatmovement.render.TotemActivation.activate((EntityLivingBase)(Object)this);ci.cancel();}
 }
}
