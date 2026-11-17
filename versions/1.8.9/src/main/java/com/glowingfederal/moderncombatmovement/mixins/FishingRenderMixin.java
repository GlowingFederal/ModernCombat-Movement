package com.glowingfederal.moderncombatmovement.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=net.minecraft.client.renderer.entity.RenderFish.class,remap=false)
public abstract class FishingRenderMixin {
 @ModifyConstant(method={"doRender(Lnet/minecraft/entity/projectile/EntityFishHook;DDDFF)V","func_76986_a(Lnet/minecraft/entity/projectile/EntityFishHook;DDDFF)V"},constant={@Constant(doubleValue=-.36),@Constant(doubleValue=.35,ordinal=1),@Constant(doubleValue=.35,ordinal=2)})
 private double mcm$lineHand(double value,net.minecraft.entity.projectile.EntityFishHook hook,double x,double y,double z,float yaw,float partial){return com.glowingfederal.moderncombatmovement.render.FishingHand.off(hook.angler)?-value:value;}
}
