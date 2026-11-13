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
}
