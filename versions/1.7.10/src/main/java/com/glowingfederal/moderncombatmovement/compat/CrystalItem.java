package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
public final class CrystalItem extends Item {
 public CrystalItem(){setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMisc);}
 @Override public boolean hasEffect(ItemStack stack){return true;}
 @Override public boolean onItemUse(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz){return Crystals.place(stack,p,w,x,y,z,side);}
}
