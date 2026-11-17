package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.entity.Entity.class,remap=false)
public abstract class SwimSoundMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"getSwimSound","func_145776_H"},at=@org.spongepowered.asm.mixin.injection.At("HEAD"),cancellable=true)
 private void mcm$swim(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<String> ci){if((Object)this instanceof net.minecraft.entity.player.EntityPlayer&&com.glowingfederal.moderncombatmovement.pose.PlayerHooks.active((net.minecraft.entity.player.EntityPlayer)(Object)this))ci.setReturnValue("moderncombatmovement:entity.player.swim");}
}
