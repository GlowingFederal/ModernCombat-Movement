package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=ModelBiped.class,remap=false)
public abstract class ModelMixin {
 @Shadow(aliases={"field_78117_n"}) public boolean isSneak;
 @Inject(method={"setRotationAngles","func_78087_a"},at=@At("HEAD")) private void mcm$crouch(float limb,float amount,float age,float yaw,float pitch,float scale,Entity entity,CallbackInfo ci) {
  if(Settings.rendering && entity instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)entity)) isSneak=PlayerHooks.state((EntityPlayer)entity).pose==Pose.CROUCHING;
 }
}
