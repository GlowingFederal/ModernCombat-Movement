package com.glowingfederal.moderncombatmovement.mixins.uab;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.core.handlers.ServerEventHandler",remap=false)
public abstract class TotemUabMixin {
 @Inject(method="handleTotemCheck",at=@At("HEAD"),cancellable=true) private void mcm$deathProtection(net.minecraft.entity.EntityLivingBase entity,net.minecraftforge.event.entity.living.LivingHurtEvent event,CallbackInfo ci) {
  // Death protection runs after armor/absorption and only after genuinely lethal damage.
  if(com.glowingfederal.moderncombatmovement.config.Settings.kit(false))ci.cancel();
 }
}
