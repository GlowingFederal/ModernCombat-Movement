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
 @ModifyVariable(method={"orientCamera","func_78467_g"},at=@At(value="STORE"),ordinal=1) private float mcm$camera(float value,float partial) {
  Entity entity=Minecraft.getMinecraft().renderViewEntity;
  if(Settings.rendering && entity instanceof net.minecraft.entity.player.EntityPlayer && PlayerHooks.active((net.minecraft.entity.player.EntityPlayer)entity)) return entity.yOffset-PlayerHooks.state((net.minecraft.entity.player.EntityPlayer)entity).cameraEye(partial);
  return value;
 }
}
