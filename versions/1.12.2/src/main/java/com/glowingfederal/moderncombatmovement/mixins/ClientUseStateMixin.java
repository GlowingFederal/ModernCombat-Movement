package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.entity.EntityLivingBase.class,remap=false)
public abstract class ClientUseStateMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"notifyDataManagerChange","func_184206_a"},at=@org.spongepowered.asm.mixin.injection.At("RETURN"))
 private void mcm$release(net.minecraft.network.datasync.DataParameter<?> key,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ClientUseStates.released((net.minecraft.entity.EntityLivingBase)(Object)this);}
}
