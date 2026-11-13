package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.item.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
/** Forge's item armor-slot API equips a glider without pretending that it is protective armor. */
public final class ElytraItem extends Item {
 public ElytraItem(){setUnlocalizedName("moderncombatmovement.elytra");setMaxStackSize(1);setMaxDamage(432);setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabTransport);}
 @Override public int getItemEnchantability(){return 1;}
 @Override public boolean isValidArmor(ItemStack stack,int slot,Entity wearer){return slot==1;}
 @Override public boolean getIsRepairable(ItemStack stack,ItemStack ingredient){return ingredient!=null&&com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ingredient).equals("etfuturum:phantom_membrane");}
 @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer player){
  ItemStack previous=player.inventory.armorInventory[2];if(previous!=null&&ItemStack.areItemStacksEqual(stack,previous))return stack;
  ItemStack equipped=stack.copy();equipped.stackSize=1;player.setCurrentItemOrArmor(3,equipped);
  if(!player.capabilities.isCreativeMode)--stack.stackSize;
  if(previous==null)return stack;
  if(stack.stackSize<=0||player.capabilities.isCreativeMode)return previous;
  if(!player.inventory.addItemStackToInventory(previous))player.dropPlayerItemWithRandomChoice(previous,false);return stack;
 }
 
}
