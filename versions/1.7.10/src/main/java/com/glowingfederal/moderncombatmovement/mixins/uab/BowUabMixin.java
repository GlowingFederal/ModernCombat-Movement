package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraftforge.event.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.core.handlers.ServerEventHandler",remap=false)
public abstract class BowUabMixin {
 @Inject(method="arrowNock",at=@At("HEAD"),cancellable=true)private void mcm$nock(ArrowNockEvent e,CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.compat.Ammunition.owns(e.result,e.entityPlayer.worldObj))ci.cancel();}
 @Inject(method="arrowLoose",at=@At("HEAD"),cancellable=true)private void mcm$loose(ArrowLooseEvent e,CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.compat.Ammunition.owns(e.bow,e.entityPlayer.worldObj))ci.cancel();}
}
