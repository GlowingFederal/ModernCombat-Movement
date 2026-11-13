package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.combat.MaceMechanics;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.MaceCombat",remap=false)
public abstract class MaceUabMixin {
 @Inject(method="damage",at=@At("HEAD"),cancellable=true)private static void mcm$bonus(DamageSource source,Entity victim,float damage,CallbackInfoReturnable<Float> ci){Entity e=source.getTrueSource();if(e instanceof EntityPlayer&&MaceMechanics.owns((EntityPlayer)e))ci.setReturnValue(damage);}
 @Inject(method="armor",at=@At("HEAD"),cancellable=true)private static void mcm$armor(double before,double after,DamageSource source,CallbackInfoReturnable<Double> ci){Entity e=source.getTrueSource();if(e instanceof EntityPlayer&&MaceMechanics.owns((EntityPlayer)e))ci.setReturnValue(after);}
 @Inject(method="impact",at=@At("HEAD"),cancellable=true)private static void mcm$impact(EntityLivingBase e,EntityLivingBase victim,CallbackInfo ci){if(e instanceof EntityPlayer&&MaceMechanics.owns((EntityPlayer)e)){MaceMechanics.impact((EntityPlayer)e,victim);ci.cancel();}}
}
