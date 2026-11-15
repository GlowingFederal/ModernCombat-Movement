/* Adapted from UAB ItemWindCharge/WindChargeItem; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class WindChargeItem extends Item {
 public WindChargeItem(){setUnlocalizedName("moderncombatmovement.wind_charge");setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);setTextureName("moderncombatmovement:wind_charge");}
 @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer p){WindCharges.use(stack,p);return stack;}
}
