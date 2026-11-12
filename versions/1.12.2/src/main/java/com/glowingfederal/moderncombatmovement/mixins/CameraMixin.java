package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityRenderer.class,remap=false)
public abstract class CameraMixin {
 @Redirect(method={"orientCamera","func_78467_g"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;getEyeHeight()F"),require=0) private float mcm$cameraMcp(Entity entity,float partial) { return mcm$eye(entity,partial); }
 @Redirect(method={"orientCamera","func_78467_g"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70047_e()F"),require=0) private float mcm$cameraSrg(Entity entity,float partial) { return mcm$eye(entity,partial); }
 private float mcm$eye(Entity entity,float partial) {
  if(Settings.rendering && entity instanceof net.minecraft.entity.player.EntityPlayer && PlayerHooks.active((net.minecraft.entity.player.EntityPlayer)entity)) return PlayerHooks.state((net.minecraft.entity.player.EntityPlayer)entity).cameraEye(partial);
  return entity.getEyeHeight();
 }
}
