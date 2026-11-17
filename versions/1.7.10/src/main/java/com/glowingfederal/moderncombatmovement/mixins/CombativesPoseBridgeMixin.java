package com.glowingfederal.moderncombatmovement.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Suspend only Combatives' geometry/pose selection while MCM owns a glide or Riptide action. */
@Mixin(value=net.minecraft.entity.player.EntityPlayer.class,remap=false,priority=900)
public abstract class CombativesPoseBridgeMixin {
 @Inject(method={"recalculateSize()V","updatePose()V"},at=@At("HEAD"),cancellable=true,require=0)
 private void mcm$exclusive(CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.compat.MovementCompat.exclusive((net.minecraft.entity.player.EntityPlayer)(Object)this))ci.cancel();}
}
