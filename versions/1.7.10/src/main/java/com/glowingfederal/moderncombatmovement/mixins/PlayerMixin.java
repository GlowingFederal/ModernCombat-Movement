package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
@Mixin(value=EntityPlayer.class, remap=false)
public abstract class PlayerMixin implements com.glowingfederal.moderncombatmovement.offhand.OffhandAccess,PlayerAccess,com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess {
 @org.spongepowered.asm.mixin.Shadow(aliases="field_71074_e")private net.minecraft.item.ItemStack itemInUse;
 @org.spongepowered.asm.mixin.Shadow(aliases="field_71072_f")private int itemInUseCount;
 public net.minecraft.item.ItemStack mcm$useStack(){return itemInUse;}
 public int mcm$useRemaining(){return itemInUseCount;}
 public void mcm$useRemaining(int ticks){itemInUseCount=ticks;}
 @Unique private com.glowingfederal.moderncombatmovement.offhand.OffhandInventory mcm$offhand;
 @Unique private boolean mcm$offhandUsing;
 public com.glowingfederal.moderncombatmovement.offhand.OffhandInventory mcm$offhand(){if(mcm$offhand==null)mcm$offhand=new com.glowingfederal.moderncombatmovement.offhand.OffhandInventory((EntityPlayer)(Object)this);return mcm$offhand;}
 public boolean mcm$offhandUsing(){return mcm$offhandUsing;}
 public void mcm$offhandUsing(boolean value){mcm$offhandUsing=value;}
 @Unique private final PlayerState mcm$state=new PlayerState();
 public PlayerState mcm$state() { return mcm$state; }
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD")) private void mcm$before(CallbackInfo ci) { PlayerHooks.before((EntityPlayer)(Object)this); }
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("RETURN")) private void mcm$after(CallbackInfo ci) { PlayerHooks.after((EntityPlayer)(Object)this); }
 @Inject(method={"onLivingUpdate","func_70636_d"},at=@At("HEAD")) private void mcm$input(CallbackInfo ci) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(ModernCombatMovement.proxy.local(p)) { ModernCombatMovement.proxy.input(p); PlayerHooks.update(p); }
 }
 @Inject(method={"getEyeHeight","func_70047_e"},at=@At("HEAD"),cancellable=true) private void mcm$eye(CallbackInfoReturnable<Float> ci) {
  EntityPlayer p=(EntityPlayer)(Object)this;
  if(PlayerHooks.active(p)) ci.setReturnValue(p.getDefaultEyeHeight()+mcm$state.pose.eyeHeight-1.62F);
 }


 @Inject(method={"applyEntityAttributes","func_110147_ax"},at=@At("RETURN")) private void mcm$attribute(CallbackInfo ci) {
  com.glowingfederal.moderncombatmovement.combat.CombatHooks.registerAttribute((EntityPlayer)(Object)this);
 }

 @Inject(method={"setSpawnChunk(Lnet/minecraft/util/ChunkCoordinates;Z)V","func_71063_a(Lnet/minecraft/util/ChunkCoordinates;Z)V"},at=@At("HEAD")) private void mcm$bed(net.minecraft.util.ChunkCoordinates pos,boolean forced,CallbackInfo ci) { com.glowingfederal.moderncombatmovement.compat.AnchorRespawn.clearForBed((EntityPlayer)(Object)this); }
}
