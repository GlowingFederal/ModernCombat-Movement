package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=ModelBiped.class,remap=false)
public abstract class ModelMixin {
 @org.spongepowered.asm.mixin.Unique private float mcm$spearSwing;
 @org.spongepowered.asm.mixin.Unique private boolean mcm$spear;
 @Inject(method={"setRotationAngles","func_78087_a"},at=@At("HEAD")) private void mcm$crouch(float limb,float amount,float age,float yaw,float pitch,float scale,Entity entity,CallbackInfo ci) {
  if(entity instanceof EntityPlayer)((ModelBiped)(Object)this).heldItemLeft=com.glowingfederal.moderncombatmovement.offhand.Offhands.get((EntityPlayer)entity)==null?0:1;
  mcm$spear=entity instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.render.SpearPoses.owned((EntityPlayer)entity);
  if(mcm$spear){mcm$spearSwing=((ModelBiped)(Object)this).swingProgress;((ModelBiped)(Object)this).swingProgress=0;}

  if(Settings.rendering && entity instanceof EntityPlayer && PlayerHooks.active((EntityPlayer)entity)) ((ModelBiped)(Object)this).isSneak=PlayerHooks.state((EntityPlayer)entity).pose==Pose.CROUCHING;
 }
 @Inject(method={"setRotationAngles","func_78087_a"},at=@At("RETURN")) private void mcm$crossbow(float limb,float amount,float age,float yaw,float pitch,float scale,Entity entity,CallbackInfo ci) {
  if(entity instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying((EntityPlayer)entity)){((ModelBiped)(Object)this).bipedHead.rotateAngleX=-(float)Math.PI/4;((ModelBiped)(Object)this).bipedHeadwear.rotateAngleX=-(float)Math.PI/4;}

  if(mcm$spear){((ModelBiped)(Object)this).swingProgress=mcm$spearSwing;com.glowingfederal.moderncombatmovement.render.SpearPoses.apply((ModelBiped)(Object)this,(EntityPlayer)entity,age,mcm$spearSwing);}

  if(Settings.rendering && entity instanceof EntityPlayer) {com.glowingfederal.moderncombatmovement.render.CrossbowPoses.apply((ModelBiped)(Object)this,(EntityPlayer)entity,age);com.glowingfederal.moderncombatmovement.render.OffhandClient.pose((ModelBiped)(Object)this,(EntityPlayer)entity);}
 }
}
