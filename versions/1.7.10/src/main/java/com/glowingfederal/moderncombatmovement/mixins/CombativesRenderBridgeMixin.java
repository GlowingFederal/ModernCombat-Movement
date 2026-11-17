package com.glowingfederal.moderncombatmovement.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=net.minecraft.entity.player.EntityPlayer.class,remap=false,priority=900)
public abstract class CombativesRenderBridgeMixin {
 @Inject(method="getSwimAnimation(F)F",at=@At("HEAD"),cancellable=true,require=0)
 private void mcm$exclusive(float partial,CallbackInfoReturnable<Float> ci){if(com.glowingfederal.moderncombatmovement.compat.MovementCompat.exclusive((net.minecraft.entity.player.EntityPlayer)(Object)this))ci.setReturnValue(0F);}
}
