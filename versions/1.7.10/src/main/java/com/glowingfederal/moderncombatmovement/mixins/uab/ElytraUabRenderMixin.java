package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra",remap=false)
public abstract class ElytraUabRenderMixin {
 @Inject(method="doRenderLayer",at=@At("HEAD"),cancellable=true)private static void mcm$oneWingLayer(EntityLivingBase e,float limb,float amount,float partial,float age,float scale,CallbackInfo ci){if(e instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.managed((EntityPlayer)e))ci.cancel();}
}
