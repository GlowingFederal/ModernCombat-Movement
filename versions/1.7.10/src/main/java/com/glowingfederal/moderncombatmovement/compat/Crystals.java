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
  cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(PlacedCrystal.class,"placed_crystal",1,ModernCombatMovement.instance,256,1,false);
  if(!Content.uab){Item item=new CrystalItem().setUnlocalizedName("moderncombatmovement.end_crystal");item.setTextureName("moderncombatmovement:end_crystal");cpw.mods.fml.common.registry.GameRegistry.registerItem(item,"end_crystal");Content.ITEMS.put("end_crystal",item);cpw.mods.fml.common.registry.GameRegistry.addRecipe(new ItemStack(item),"GGG","GEG","GTG",'G',Blocks.glass,'E',Items.ender_eye,'T',Items.ghast_tear);}
 }
 public static boolean place(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side){
  if(!Settings.kit(w.isRemote))return false;
  net.minecraft.block.Block floor=w.getBlock(x,y,z);
  if(floor!=Blocks.obsidian&&floor!=Blocks.bedrock)return false;
  if(!p.canPlayerEdit(x,y+1,z,side,stack))return false;
  if(!w.isAirBlock(x,y+1,z))return false;
  if(!w.getEntitiesWithinAABBExcludingEntity(null,net.minecraft.util.AxisAlignedBB.getBoundingBox(x,y+1,z,x+1,y+3,z+1)).isEmpty())return false;
  if(!w.isRemote){
   EntityEnderCrystal crystal=new PlacedCrystal(w);
   if(Content.uab)try{Class<?> type=Class.forName("ganymedes01.etfuturum.entities.EntityPlacedEndCrystal");crystal=(EntityEnderCrystal)type.getConstructor(World.class).newInstance(w);type.getMethod("setBlockPos",int.class,int.class,int.class).invoke(crystal,x,y,z);}catch(ReflectiveOperationException failure){throw new IllegalStateException("UAB crystal adapter changed",failure);}
   crystal.setLocationAndAngles(x+.5,y+1,z+.5,0,0);
   w.spawnEntityInWorld(crystal);

  }
  --stack.stackSize;return true;
 }
}
