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
@Mixin(targets="ganymedes01.etfuturum.items.ItemCrossbow",remap=false)
public abstract class CrossbowUabMixin extends Item {
 @Inject(method={"getMaxItemUseDuration","func_77626_a"},at=@At("HEAD"),cancellable=true) private void mcm$duration(ItemStack stack,CallbackInfoReturnable<Integer> ci) { if(Settings.kit(false))ci.setReturnValue(Crossbows.duration(stack)); }
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true) private void mcm$use(ItemStack stack,World world,EntityPlayer player,CallbackInfoReturnable<ItemStack> ci) { if(Settings.kit(world.isRemote)) { Crossbows.use(stack,player);ci.setReturnValue(stack); } }
 @Inject(method={"onPlayerStoppedUsing","func_77615_a"},at=@At("HEAD"),cancellable=true) private void mcm$release(ItemStack stack,World world,EntityPlayer user,int remaining,CallbackInfo ci) { if(Settings.kit(world.isRemote) && user instanceof EntityPlayer) { Crossbows.release(stack,(EntityPlayer)user,remaining);ci.cancel(); } }
 @Override public void onUsingTick(ItemStack stack,EntityPlayer player,int remaining) { if(Settings.kit(player.worldObj.isRemote))Crossbows.tick(stack,player,remaining); }
}
