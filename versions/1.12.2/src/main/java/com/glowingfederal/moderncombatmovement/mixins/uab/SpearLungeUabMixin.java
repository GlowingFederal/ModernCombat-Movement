package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.SpearLunge",remap=false)
public abstract class SpearLungeUabMixin {
 @Inject(method="swing",at=@At("HEAD"),cancellable=true)private static void mcm$lunge(EntityPlayerMP p,ItemStack s,CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.combat.SpearMechanics.owned(p))ci.cancel();}
}
