package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
/** Exact native/UAB crystal ownership; foreign subclasses keep their own lifecycle. */
public final class Crystals {
 private Crystals(){}
 public static boolean known(Entity e){return e.getClass()==EntityEnderCrystal.class||e instanceof PlacedCrystal||e.getClass().getName().equals("ganymedes01.etfuturum.entities.EntityPlacedEndCrystal");}
 public static void register(){
  net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(new CrystalRespawn());
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(RespawnedDragon.class,"respawned_dragon",8,ModernCombatMovement.instance,256,3,true);
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(PlacedCrystal.class,"placed_crystal",1,ModernCombatMovement.instance,256,1,false);
  if(!Content.uab){Item item=new CrystalItem().setUnlocalizedName("moderncombatmovement.end_crystal");net.minecraftforge.fml.common.registry.GameRegistry.registerItem(item,"end_crystal");Content.ITEMS.put("end_crystal",item);net.minecraftforge.fml.common.registry.GameRegistry.addRecipe(new ItemStack(item),"GGG","GEG","GTG",'G',Blocks.glass,'E',Items.ender_eye,'T',Items.ghast_tear);}
 }
 public static boolean place(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side){
  if(!Settings.kit(w.isRemote))return false;
  net.minecraft.util.BlockPos pos=new net.minecraft.util.BlockPos(x,y,z);net.minecraft.block.Block floor=w.getBlockState(pos).getBlock();
  if(floor!=Blocks.obsidian&&floor!=Blocks.bedrock)return false;
  if(!p.canPlayerEdit(pos.up(),EnumFacing.getFront(side),stack))return false;
  if(!w.isAirBlock(pos.up()))return false;
  if(!w.getEntitiesWithinAABBExcludingEntity(null,new net.minecraft.util.AxisAlignedBB(x,y+1,z,x+1,y+3,z+1)).isEmpty())return false;
  if(!w.isRemote){
   EntityEnderCrystal crystal=new PlacedCrystal(w);

   crystal.setLocationAndAngles(x+.5,y+1,z+.5,0,0);
   if(!w.spawnEntityInWorld(crystal))return false;
   CrystalRespawn.placed(crystal);

  }
  if(!p.capabilities.isCreativeMode)--stack.stackSize;return true;
 }
}
