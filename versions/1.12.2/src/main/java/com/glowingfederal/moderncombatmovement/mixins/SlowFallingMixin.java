package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.KitEffects;
/** Modern LivingEntity effective gravity; native collisions and travel remain authoritative. */
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class SlowFallingMixin {
 @Inject(method={"onLivingUpdate","func_70636_d"},at=@At("HEAD"))private void mcm$fall(CallbackInfo ci){EntityLivingBase e=(EntityLivingBase)(Object)this;if(KitEffects.has(e,4))e.fallDistance=0;}
 @ModifyConstant(method={"travel","func_191986_a"},constant=@Constant(doubleValue=.08))private double mcm$airGravity(double original){return KitEffects.gravity((EntityLivingBase)(Object)this,original);}
 @ModifyConstant(method={"travel","func_191986_a"},constant=@Constant(doubleValue=.02))private double mcm$fluidGravity(double original){EntityLivingBase e=(EntityLivingBase)(Object)this;double gravity=KitEffects.gravity(e,.08);return gravity==.08?original:gravity/(e.isInWater()?16:4);}
}
