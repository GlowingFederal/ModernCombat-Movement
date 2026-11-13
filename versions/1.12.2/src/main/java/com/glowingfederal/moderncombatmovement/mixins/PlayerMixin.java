package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
@Mixin(value=EntityPlayer.class, remap=false)
public abstract class PlayerMixin implements PlayerAccess {
 @Unique private final PlayerState mcm$state=new PlayerState();
 public PlayerState mcm$state() { return mcm$state; }
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD")) private void mcm$before(CallbackInfo ci) { PlayerHooks.before((EntityPlayer)(Object)this); }
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("RETURN")) private void mcm$after(CallbackInfo ci) { PlayerHooks.after((EntityPlayer)(Object)this); }
 @Inject(method={"onLivingUpdate","func_70636_d"},at=@At("HEAD")) private void mcm$input(CallbackInfo ci) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(ModernCombatMovement.proxy.local(p)) { ModernCombatMovement.proxy.input(p); PlayerHooks.update(p); }
 }
 @Inject(method={"getEyeHeight","func_70047_e"},at=@At("HEAD"),cancellable=true) private void mcm$eye(CallbackInfoReturnable<Float> ci) {
  if(PlayerHooks.active((EntityPlayer)(Object)this)) ci.setReturnValue(mcm$state.pose.eyeHeight);
 }
 @Inject(method={"updateSize","func_184808_cD"},at=@At("HEAD"),cancellable=true) private void mcm$size(CallbackInfo ci) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(PlayerHooks.active(p)) { PlayerHooks.dimensions(p); ci.cancel(); }
 }

 @Inject(method={"applyEntityAttributes","func_110147_ax"},at=@At("RETURN")) private void mcm$attribute(CallbackInfo ci) {
  com.glowingfederal.moderncombatmovement.combat.CombatHooks.registerAttribute((EntityPlayer)(Object)this);
 }

 @Inject(method={"setSpawnPoint","func_180473_a"},at=@At("HEAD")) private void mcm$bed(net.minecraft.util.math.BlockPos pos,boolean forced,CallbackInfo ci) { com.glowingfederal.moderncombatmovement.compat.AnchorRespawn.clearForBed((EntityPlayer)(Object)this); }
}
