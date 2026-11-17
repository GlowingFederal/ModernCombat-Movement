package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.Shields;
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class ShieldDamageMixin implements com.glowingfederal.moderncombatmovement.combat.ShieldBlockAccess {
 @Unique private boolean mcm$shieldBlocked;
 public boolean mcm$fullyBlocked(){return mcm$shieldBlocked;}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD"))private void mcm$begin(DamageSource source,float damage,CallbackInfoReturnable<Boolean> ci){mcm$shieldBlocked=false;}
 @Inject(method={"canBlockDamageSource","func_184583_d"},at=@At("HEAD"),cancellable=true)private void mcm$block(DamageSource source,CallbackInfoReturnable<Boolean> ci){EntityLivingBase e=(EntityLivingBase)(Object)this;if(Shields.owns(e)){mcm$shieldBlocked=Shields.canBlock(e,source);ci.setReturnValue(mcm$shieldBlocked);}}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("RETURN"))private void mcm$end(DamageSource source,float damage,CallbackInfoReturnable<Boolean> ci){mcm$shieldBlocked=false;}
}
