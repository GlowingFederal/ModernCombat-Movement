package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
public final class CrossbowItem extends Item {
 public CrossbowItem() { setMaxStackSize(1);setMaxDamage(465);setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);setUnlocalizedName("moderncombatmovement.crossbow").setTextureName("moderncombatmovement:crossbow_standby"); }
 @Override public int getMaxItemUseDuration(ItemStack stack) { return Crossbows.duration(stack); }
 @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.none; }
 @Override public int getItemEnchantability() { return 1; }
 @Override public ItemStack onItemRightClick(ItemStack stack,World w,EntityPlayer p) { Crossbows.use(stack,p);return stack; }
 @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityPlayer user,int remaining) { if(user instanceof EntityPlayer) Crossbows.release(stack,(EntityPlayer)user,remaining); }
 @Override public void onUsingTick(ItemStack stack,EntityPlayer user,int remaining) { if(user instanceof EntityPlayer) Crossbows.tick(stack,(EntityPlayer)user,remaining); }
 @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) public net.minecraft.util.IIcon getIcon(ItemStack stack,int pass,EntityPlayer p,ItemStack using,int remaining){return com.glowingfederal.moderncombatmovement.render.CrossbowIcons.icon(stack,p);}
 @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) public net.minecraft.util.IIcon getIcon(ItemStack stack,int pass){return com.glowingfederal.moderncombatmovement.render.CrossbowIcons.icon(stack,null);}
}
