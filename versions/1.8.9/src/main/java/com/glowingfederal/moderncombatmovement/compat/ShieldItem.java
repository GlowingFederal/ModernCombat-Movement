/* Adapted from UAB ItemShield; LGPL-3.0, see LICENSES/UAB-LGPL-3.0.txt. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
public final class ShieldItem extends Item {
 public ShieldItem(){setMaxStackSize(1);setMaxDamage(336);setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);setUnlocalizedName("moderncombatmovement.shield");}
 @Override public int getMaxItemUseDuration(ItemStack stack){return 72000;}
 @Override public EnumAction getItemUseAction(ItemStack stack){return EnumAction.BLOCK;}
 @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer player){Shields.use(stack,player);return stack;}
 @Override public void onUsingTick(ItemStack stack,EntityPlayer player,int remaining){if(!Shields.enabled(player)||Shields.cooldown(player)>0)player.clearItemInUse();}
 @Override public boolean getIsRepairable(ItemStack shield,ItemStack ingredient){for(int id:OreDictionary.getOreIDs(ingredient))if(OreDictionary.getOreName(id).equals("plankWood"))return true;return false;}
}
