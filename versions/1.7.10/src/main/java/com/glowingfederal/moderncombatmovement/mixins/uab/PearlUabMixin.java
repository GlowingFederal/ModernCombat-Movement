package com.glowingfederal.moderncombatmovement.mixins.uab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.core.handlers.ServerEventHandler",remap=false)
public abstract class PearlUabMixin {
 @Redirect(method="teleportEvent",at=@At(value="INVOKE",target="Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$mite(net.minecraft.world.World world,net.minecraft.entity.Entity entity){return !com.glowingfederal.moderncombatmovement.compat.PearlTeleport.active()||!entity.getClass().getName().equals("ganymedes01.etfuturum.entities.EntityEndermite")?world.spawnEntityInWorld(entity):false;}
 @Redirect(method="teleportEvent",at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72838_d(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$miteSrg(net.minecraft.world.World world,net.minecraft.entity.Entity entity){return mcm$mite(world,entity);}
}
