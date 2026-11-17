package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.*;
public final class AnchorBlock extends Block {
 @Override public boolean onBlockActivated(World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state,net.minecraft.entity.player.EntityPlayer player,net.minecraft.util.EnumFacing side,float hx,float hy,float hz){return Anchors.use(world,pos.getX(),pos.getY(),pos.getZ(),player,player.getHeldItem());}
 public static final net.minecraft.block.properties.PropertyInteger CHARGE=net.minecraft.block.properties.PropertyInteger.create("charges",0,4);
 public AnchorBlock() { super(Material.rock); setUnlocalizedName("moderncombatmovement.respawn_anchor"); setHardness(50);setResistance(1200);setHarvestLevel("pickaxe",3);setCreativeTab(CreativeTabs.tabBlock); setDefaultState(blockState.getBaseState().withProperty(CHARGE,0)); }
 @Override protected net.minecraft.block.state.BlockState createBlockState() { return new net.minecraft.block.state.BlockState(this,CHARGE); }
 @Override public net.minecraft.block.state.IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(CHARGE,Math.min(4,Math.max(0,meta))); }
 @Override public int getMetaFromState(net.minecraft.block.state.IBlockState state) { return state.getValue(CHARGE); }
 @Override public int damageDropped(net.minecraft.block.state.IBlockState state) { return 0; }
 @Override public int getMobilityFlag() { return 2; }
 @Override public int getLightValue(IBlockAccess world,net.minecraft.util.BlockPos pos) { return getMetaFromState(world.getBlockState(pos))*15/4; }
 @Override public boolean hasComparatorInputOverride() { return true; }
 @Override public int getComparatorInputOverride(World world,net.minecraft.util.BlockPos pos) { return getMetaFromState(world.getBlockState(pos))*15/4; }

}
