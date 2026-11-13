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
 public static boolean known(Entity e){return e.getClass()==EntityEnderCrystal.class||e.getClass().getName().equals("ganymedes01.etfuturum.entities.EntityPlacedEndCrystal");}
 public static void register(){


 }
 public static boolean place(ItemStack stack,EntityPlayer p,World w,int x,int y,int z,int side){
  if(!Settings.kit(w.isRemote))return false;
  net.minecraft.util.math.BlockPos pos=new net.minecraft.util.math.BlockPos(x,y,z);net.minecraft.block.Block floor=w.getBlockState(pos).getBlock();
  if(floor!=Blocks.OBSIDIAN&&floor!=Blocks.BEDROCK)return false;
  if(!p.canPlayerEdit(pos.up(),EnumFacing.byIndex(side),stack))return false;
  if(!w.isAirBlock(pos.up()))return false;
  if(!w.getEntitiesWithinAABBExcludingEntity(null,new net.minecraft.util.math.AxisAlignedBB(x,y+1,z,x+1,y+3,z+1)).isEmpty())return false;
  if(!w.isRemote){
   EntityEnderCrystal crystal=new EntityEnderCrystal(w,x+.5,y+1,z+.5);

   crystal.setLocationAndAngles(x+.5,y+1,z+.5,0,0);crystal.setShowBottom(false);
   w.spawnEntity(crystal);
   if(w.provider instanceof WorldProviderEnd){net.minecraft.world.end.DragonFightManager fight=((WorldProviderEnd)w.provider).getDragonFightManager();if(fight!=null)fight.respawnDragon();}
  }
  stack.shrink(1);return true;
 }
}
