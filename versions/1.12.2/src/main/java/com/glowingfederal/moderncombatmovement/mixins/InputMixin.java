package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovementInput;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=MovementInputFromOptions.class,remap=false)
public abstract class InputMixin {
 @Inject(method={"updatePlayerMoveState","func_78898_a"},at=@At("HEAD"))private void mcm$controls(CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ClientControls.poll();}
 @Inject(method={"updatePlayerMoveState","func_78898_a"},at=@At("RETURN")) private void mcm$input(CallbackInfo ci) {
  EntityPlayer p=Minecraft.getMinecraft().player; if(p==null || !Settings.movement(true)||com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(p)) return;
  MovementInput i=(MovementInput)(Object)this;
  if(i.sneak) { i.moveStrafe/=.3F; i.moveForward/=.3F; }
  // The legacy travel method normalizes above unit length. Keep the modern square-input
  // slowdown result: diagonal crouch input is .3 on each axis, not .3/sqrt(2).
  PlayerState s=PlayerHooks.state(p);
  boolean slow=!p.capabilities.isFlying && !s.swimming && (i.sneak || s.pose==Pose.CROUCHING || s.pose==Pose.LOW && !p.isInWater());
  if(slow) { i.moveStrafe*=.3F; i.moveForward*=.3F; }
 }
}
