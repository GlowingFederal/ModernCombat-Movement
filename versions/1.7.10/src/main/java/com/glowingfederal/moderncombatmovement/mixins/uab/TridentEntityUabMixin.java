package com.glowingfederal.moderncombatmovement.mixins.uab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.entities.EntityTrident",remap=false)
public abstract class TridentEntityUabMixin {
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD"),cancellable=true)private void mcm$canonical(CallbackInfo ci){if(((Object)this).getClass().getName().equals("ganymedes01.etfuturum.entities.EntityTrident")&&com.glowingfederal.moderncombatmovement.compat.UabTridentBridge.queue((net.minecraft.entity.Entity)(Object)this))ci.cancel();}
}
