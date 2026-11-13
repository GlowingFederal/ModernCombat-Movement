package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
@Mixin(value=ItemFirework.class,remap=false,priority=900)
public abstract class FireworkUseMixin extends Item {
 // Legacy ItemFirework inherits this method. This pass-through supplies an injection point;
 // a higher-priority UAB override is retained, and the same canonical hook adapts its body.
 @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer p){return super.onItemRightClick(stack,world,p);}

 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$boost(ItemStack stack,World w,EntityPlayer p,CallbackInfoReturnable<ItemStack> ci){if((Object)this!=net.minecraft.init.Items.fireworks||!FlightHooks.managed(p)||!FlightHooks.flying(p))return;if(!w.isRemote){w.spawnEntityInWorld(new com.glowingfederal.moderncombatmovement.compat.BoostRocket(w,p,stack));if(!p.capabilities.isCreativeMode)--stack.stackSize;}ci.setReturnValue(stack);}
 @Inject(method={"onItemUse","func_77648_a"},at=@At("HEAD"),cancellable=true)private void mcm$airPriority(ItemStack s,EntityPlayer p,World w,int x,int y,int z,int side,float a,float b,float c,CallbackInfoReturnable<Boolean> ci){if((Object)this==net.minecraft.init.Items.fireworks&&FlightHooks.managed(p)&&FlightHooks.flying(p))ci.setReturnValue(false);}
}
