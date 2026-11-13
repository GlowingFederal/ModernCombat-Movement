package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=LayerElytra.class,remap=false)
public abstract class ElytraRenderMixin {
 @Inject(method={"doRenderLayer","func_177141_a"},at=@At("HEAD"),cancellable=true)private void mcm$modernWingTick(EntityLivingBase e,float limb,float amount,float partial,float age,float yaw,float pitch,float scale,CallbackInfo ci){if(e instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.managed((EntityPlayer)e)){com.glowingfederal.moderncombatmovement.render.ElytraWings.render((EntityPlayer)e,partial,scale);ci.cancel();}}
}
