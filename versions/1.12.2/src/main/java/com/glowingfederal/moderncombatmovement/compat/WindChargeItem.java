/* Adapted from UAB ItemWindCharge/WindChargeItem; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class WindChargeItem extends Item {
 public WindChargeItem(){setTranslationKey("moderncombatmovement.wind_charge");setCreativeTab(net.minecraft.creativetab.CreativeTabs.COMBAT);}
 @Override public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer p,net.minecraft.util.EnumHand hand){ItemStack stack=p.getHeldItem(hand);return new net.minecraft.util.ActionResult<ItemStack>(WindCharges.use(stack,p)?net.minecraft.util.EnumActionResult.SUCCESS:net.minecraft.util.EnumActionResult.FAIL,stack);}
}
