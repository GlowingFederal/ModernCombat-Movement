package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.KitEffects;
@Mixin(value=Entity.class,remap=false)
public abstract class WebMovementMixin {
 @ModifyConstant(method={"moveEntity","func_70091_d"},constant=@Constant(doubleValue=.25)) private double mcm$horizontalWeb(double nativeValue) { return KitEffects.weavingWeb((Entity)(Object)this) ? .5 : nativeValue; }
 @ModifyConstant(method={"moveEntity","func_70091_d"},constant=@Constant(doubleValue=.05000000074505806)) private double mcm$verticalWeb(double nativeValue) { return KitEffects.weavingWeb((Entity)(Object)this) ? .25 : nativeValue; }
 @Inject(method={"moveEntity","func_70091_d"},at=@At("RETURN")) private void mcm$clearWeb(double x,double y,double z,CallbackInfo ci) { ((Entity)(Object)this).getEntityData().removeTag("MCMWeavingWeb"); }
}
