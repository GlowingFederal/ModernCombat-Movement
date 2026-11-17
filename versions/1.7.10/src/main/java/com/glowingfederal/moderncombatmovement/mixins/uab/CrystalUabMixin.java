package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** UAB identities and old saved data remain; only MCM owns the active respawn sequence. */
@Pseudo @Mixin(targets="ganymedes01.etfuturum.entities.EntityPlacedEndCrystal",remap=false)
public abstract class CrystalUabMixin extends EntityEnderCrystal {
 protected CrystalUabMixin(World w){super(w);}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD"),cancellable=true)
 private void mcm$ritual(CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.config.Settings.kit(worldObj.isRemote)){super.onUpdate();if(!worldObj.isRemote&&ticksExisted%20==0)com.glowingfederal.moderncombatmovement.compat.CrystalRespawn.placed(this);ci.cancel();}}
}
