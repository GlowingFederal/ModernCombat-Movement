package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=LayerCape.class,remap=false)
public abstract class ElytraRenderMixin {
 @Inject(method={"doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V","func_177141_a(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V"},at=@At("HEAD"),cancellable=true)private void mcm$cape(AbstractClientPlayer p,float limb,float amount,float partial,float age,float yaw,float pitch,float scale,CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.managed(p))ci.cancel();}
}
