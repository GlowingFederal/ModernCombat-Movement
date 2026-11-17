package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
@Mixin(value=ItemFirework.class,remap=false,priority=1200)
public abstract class FireworkUseMixin {
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"))private void mcm$modernDefault(World w,EntityPlayer p,net.minecraft.util.EnumHand hand,CallbackInfoReturnable<net.minecraft.util.ActionResult<ItemStack>> ci){if((Object)this!=net.minecraft.init.Items.FIREWORKS||!FlightHooks.managed(p)||!FlightHooks.flying(p))return;ItemStack stack=p.getHeldItem(hand);if(!stack.hasTagCompound()||!stack.getTagCompound().hasKey("Fireworks",10)){net.minecraft.nbt.NBTTagCompound root=stack.hasTagCompound()?stack.getTagCompound():new net.minecraft.nbt.NBTTagCompound(),fireworks=new net.minecraft.nbt.NBTTagCompound();fireworks.setByte("Flight",(byte)1);root.setTag("Fireworks",fireworks);stack.setTagCompound(root);}}

 
 @Inject(method={"onItemUse","func_180614_a"},at=@At("HEAD"),cancellable=true)private void mcm$airPriority(EntityPlayer p,World w,net.minecraft.util.math.BlockPos pos,net.minecraft.util.EnumHand hand,net.minecraft.util.EnumFacing side,float a,float b,float c,CallbackInfoReturnable<net.minecraft.util.EnumActionResult> ci){if((Object)this==net.minecraft.init.Items.FIREWORKS&&FlightHooks.managed(p)&&FlightHooks.flying(p))ci.setReturnValue(net.minecraft.util.EnumActionResult.PASS);}
 @Group(name="mcmGroundRocketOwner",min=1) @Redirect(method={"onItemUse","func_180614_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),require=0,expect=0)private boolean mcm$groundMcp(World world,net.minecraft.entity.Entity entity,EntityPlayer player,World methodWorld,net.minecraft.util.math.BlockPos pos,net.minecraft.util.EnumHand hand,net.minecraft.util.EnumFacing side,float hx,float hy,float hz){if((Object)this==net.minecraft.init.Items.FIREWORKS)com.glowingfederal.moderncombatmovement.compat.GroundRockets.launched(entity,player);return world.spawnEntity(entity);}
 @Group(name="mcmGroundRocketOwner",min=1) @Redirect(method={"onItemUse","func_180614_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72838_d(Lnet/minecraft/entity/Entity;)Z"),require=0,expect=0)private boolean mcm$groundSrg(World world,net.minecraft.entity.Entity entity,EntityPlayer player,World methodWorld,net.minecraft.util.math.BlockPos pos,net.minecraft.util.EnumHand hand,net.minecraft.util.EnumFacing side,float hx,float hy,float hz){if((Object)this==net.minecraft.init.Items.FIREWORKS)com.glowingfederal.moderncombatmovement.compat.GroundRockets.launched(entity,player);return world.spawnEntity(entity);}
}
