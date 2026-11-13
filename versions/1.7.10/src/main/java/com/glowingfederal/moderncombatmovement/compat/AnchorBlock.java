package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.*;
public final class AnchorBlock extends Block {

 public AnchorBlock() { super(Material.rock); setBlockName("moderncombatmovement.respawn_anchor"); setHardness(50);setResistance(1200);setHarvestLevel("pickaxe",3);setCreativeTab(CreativeTabs.tabBlock);  }
 @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) private net.minecraft.util.IIcon[] icons;
 @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister atlas) {
  icons=new net.minecraft.util.IIcon[8]; for(int i=0;i<5;i++) icons[i]=atlas.registerIcon("moderncombatmovement:respawn_anchor_side"+i); icons[5]=atlas.registerIcon("moderncombatmovement:respawn_anchor_top");icons[6]=atlas.registerIcon("moderncombatmovement:respawn_anchor_top_off");icons[7]=atlas.registerIcon("moderncombatmovement:respawn_anchor_bottom");
 }
 @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT) public net.minecraft.util.IIcon getIcon(int side,int meta) { return icons[side==0 ? 7 : side==1 ? meta==0 ? 6 : 5 : Math.min(4,meta)]; }
 @Override public int damageDropped(int meta) { return 0; }
 @Override public int getLightValue(IBlockAccess access,int x,int y,int z) { return Math.min(4,access.getBlockMetadata(x,y,z))*15/4; }
 @Override public boolean hasComparatorInputOverride() { return true; }
 @Override public int getComparatorInputOverride(World w,int x,int y,int z,int side) { return Math.min(4,w.getBlockMetadata(x,y,z))*15/4; }
 @Override public int getMobilityFlag() { return 2; }
}
