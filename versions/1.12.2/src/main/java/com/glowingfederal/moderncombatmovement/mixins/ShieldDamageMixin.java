package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.Shields;
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class ShieldDamageMixin {
 @Inject(method={"canBlockDamageSource","func_184583_d"},at=@At("HEAD"),cancellable=true)private void mcm$block(DamageSource source,CallbackInfoReturnable<Boolean> ci){EntityLivingBase e=(EntityLivingBase)(Object)this;if(Shields.owns(e))ci.setReturnValue(Shields.canBlock(e,source));}
}
