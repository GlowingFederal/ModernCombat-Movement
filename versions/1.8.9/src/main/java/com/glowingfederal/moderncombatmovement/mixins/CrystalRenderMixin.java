package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=RenderEnderCrystal.class,remap=false)
public abstract class CrystalRenderMixin {
 @Unique private final ModelBase mcm$noBase=new ModelEnderCrystal(0,false);

 @Redirect(method={"doRender","func_76986_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"),require=0)
 private void mcm$mcp(ModelBase model,Entity entity,float a,float b,float c,float d,float e,float scale){if(entity instanceof com.glowingfederal.moderncombatmovement.compat.PlacedCrystal)model=mcm$noBase;model.render(entity,a,b,c,d,e,scale);}

 @Redirect(method={"doRender","func_76986_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/model/ModelBase;func_78088_a(Lnet/minecraft/entity/Entity;FFFFFF)V"),require=0)
 private void mcm$srg(ModelBase model,Entity entity,float a,float b,float c,float d,float e,float scale){if(entity instanceof com.glowingfederal.moderncombatmovement.compat.PlacedCrystal)model=mcm$noBase;model.render(entity,a,b,c,d,e,scale);}

 @org.spongepowered.asm.mixin.injection.Inject(method={"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V","func_76986_a(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"},at=@At("RETURN"))
 private void mcm$beam(net.minecraft.entity.item.EntityEnderCrystal crystal,double x,double y,double z,float yaw,float partial,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.CrystalBeamRender.draw(crystal,x,y,z,partial);}
}
