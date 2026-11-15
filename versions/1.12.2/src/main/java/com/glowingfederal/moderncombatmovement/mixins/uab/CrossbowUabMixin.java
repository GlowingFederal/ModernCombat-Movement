package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.Crossbows;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.CrossbowItem",remap=false)
public abstract class CrossbowUabMixin extends Item {
 @Inject(method={"getMaxItemUseDuration","func_77626_a"},at=@At("HEAD"),cancellable=true) private void mcm$duration(ItemStack stack,CallbackInfoReturnable<Integer> ci) { if(Settings.projectiles(false))ci.setReturnValue(Crossbows.duration(stack)); }
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true) private void mcm$use(World world,EntityPlayer player,net.minecraft.util.EnumHand hand,CallbackInfoReturnable<net.minecraft.util.ActionResult<ItemStack>> ci) { if(Settings.projectiles(world.isRemote)) { ItemStack stack=player.getHeldItem(hand);ci.setReturnValue(new net.minecraft.util.ActionResult<ItemStack>(Crossbows.use(stack,player,hand)?net.minecraft.util.EnumActionResult.SUCCESS:net.minecraft.util.EnumActionResult.FAIL,stack)); } }
 @Inject(method={"onPlayerStoppedUsing","func_77615_a"},at=@At("HEAD"),cancellable=true) private void mcm$release(ItemStack stack,World world,EntityLivingBase user,int remaining,CallbackInfo ci) { if(Settings.projectiles(world.isRemote) && user instanceof EntityPlayer) { Crossbows.release(stack,(EntityPlayer)user,remaining);ci.cancel(); } }
 @Inject(method="onUsingTick",at=@At("HEAD"),cancellable=true) private void mcm$tick(ItemStack stack,EntityLivingBase user,int remaining,CallbackInfo ci) { if(Settings.projectiles(user.world.isRemote) && user instanceof EntityPlayer) { Crossbows.tick(stack,(EntityPlayer)user,remaining);ci.cancel(); } }
 @Inject(method={"getItemUseAction","func_77661_b"},at=@At("HEAD"),cancellable=true) private void mcm$action(ItemStack stack,CallbackInfoReturnable<net.minecraft.item.EnumAction> ci) { if(Settings.projectiles(false))ci.setReturnValue(net.minecraft.item.EnumAction.NONE); }
}

