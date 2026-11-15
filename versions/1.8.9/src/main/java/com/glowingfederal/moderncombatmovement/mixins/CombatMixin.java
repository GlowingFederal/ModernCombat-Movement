package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.api.ItemProperties;
import com.glowingfederal.moderncombatmovement.combat.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class CombatMixin {
 @Inject(method={"attackTargetEntityWithCurrentItem","func_71059_n"},at=@At("HEAD"),cancellable=true) private void mcm$attack(net.minecraft.entity.Entity target,CallbackInfo ci) {
  EntityPlayer p=(EntityPlayer)(Object)this; if(p.isUsingItem()){ci.cancel();return;} ItemProperties profile=CombatHooks.profile(p);
  if(SpearMechanics.owned(p)){SpearMechanics.jab(p);ci.cancel();return;}
  if(profile!=null && Settings.combat(p.worldObj.isRemote)) { LegacyAttack.attack(p,target,profile); ci.cancel(); }
 }
}
