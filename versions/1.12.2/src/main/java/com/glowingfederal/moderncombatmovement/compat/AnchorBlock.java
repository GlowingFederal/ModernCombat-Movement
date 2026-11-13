package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.*;
public final class AnchorBlock extends Block {
 public static final net.minecraft.block.properties.PropertyInteger CHARGE=net.minecraft.block.properties.PropertyInteger.create("charges",0,4);
 public AnchorBlock() { super(Material.ROCK); setTranslationKey("moderncombatmovement.respawn_anchor"); setHardness(50);setResistance(1200);setHarvestLevel("pickaxe",3);setCreativeTab(CreativeTabs.BUILDING_BLOCKS); setDefaultState(blockState.getBaseState().withProperty(CHARGE,0)); }
 @Override protected net.minecraft.block.state.BlockStateContainer createBlockState() { return new net.minecraft.block.state.BlockStateContainer(this,CHARGE); }
 @Override public net.minecraft.block.state.IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(CHARGE,Math.min(4,Math.max(0,meta))); }
 @Override public int getMetaFromState(net.minecraft.block.state.IBlockState state) { return state.getValue(CHARGE); }
 @Override public int damageDropped(net.minecraft.block.state.IBlockState state) { return 0; }

}
