package com.glowingfederal.moderncombatmovement.mixins.uab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.client.renderer.entity.PlacedEndCrystalRenderer",remap=false)
public abstract class CrystalRenderUabMixin {

 @org.spongepowered.asm.mixin.injection.Inject(method={"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V","func_76986_a(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"},at=@At("RETURN"))
 private void mcm$beam(net.minecraft.entity.item.EntityEnderCrystal crystal,double x,double y,double z,float yaw,float partial,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.CrystalBeamRender.draw(crystal,x,y,z,partial);}
}
