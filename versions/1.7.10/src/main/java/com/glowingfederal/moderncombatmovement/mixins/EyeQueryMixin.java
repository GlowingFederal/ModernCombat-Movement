package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.pose.*;
/** 1.7 client posY includes yOffset; native world queries must sample the pose eye above the box feet. */
@Mixin(value=Entity.class,remap=false)
public abstract class EyeQueryMixin {
 @Redirect(method={"isEntityInsideOpaqueBlock","func_70094_T","isInsideOfMaterial","func_70055_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;getEyeHeight()F"),require=0)
 private float mcm$eyeMcp(Entity e) { return mcm$queryEye(e); }
 @Redirect(method={"isEntityInsideOpaqueBlock","func_70094_T","isInsideOfMaterial","func_70055_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70047_e()F"),require=0)
 private float mcm$eyeSrg(Entity e) { return mcm$queryEye(e); }
 private float mcm$queryEye(Entity e) {
  if(e instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)e)) return (float)(e.boundingBox.minY-e.posY+PlayerHooks.state((EntityPlayer)e).pose.eyeHeight);
  return e.getEyeHeight();
 }
}
