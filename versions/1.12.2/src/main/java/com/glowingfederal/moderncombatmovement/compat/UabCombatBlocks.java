package com.glowingfederal.moderncombatmovement.compat;
/** Optional, exact UAB block boundary. No duplicate block/tile registrations or replacement block engine. */
public final class UabCombatBlocks {
 private static Class<?> ROD,BELL,CANDLE;private static java.lang.reflect.Method STRIKE,RING;
 static{if(Content.uab)try{ROD=Class.forName("ganymedes01.etfuturum.LightningRod");BELL=Class.forName("ganymedes01.etfuturum.BellBlock");CANDLE=Class.forName("ganymedes01.etfuturum.CandleBlock");STRIKE=ROD.getMethod("strike",net.minecraft.world.World.class,net.minecraft.util.math.BlockPos.class);RING=BELL.getMethod("ring",net.minecraft.world.World.class,net.minecraft.util.math.BlockPos.class);}catch(ReflectiveOperationException failure){throw new IllegalStateException("Unsupported UAB block integration",failure);}}
 private UabCombatBlocks(){}
 public static boolean rod(net.minecraft.world.World world,int x,int y,int z){return ROD!=null&&ROD.isInstance(world.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock());}
 public static void strike(net.minecraft.world.World world,int x,int y,int z){try{STRIKE.invoke(world.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock(),world,new net.minecraft.util.math.BlockPos(x,y,z));}catch(ReflectiveOperationException failure){throw new IllegalStateException(failure);}}
 public static boolean wind(net.minecraft.world.World world,int x,int y,int z){
  if(!Content.uab)return false;net.minecraft.block.Block block=world.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock();
  try{if(BELL!=null&&BELL.isInstance(block)){RING.invoke(null,world,new net.minecraft.util.math.BlockPos(x,y,z));return true;}
   if(CANDLE!=null&&CANDLE.isInstance(block)){net.minecraft.util.math.BlockPos pos=new net.minecraft.util.math.BlockPos(x,y,z);net.minecraft.block.state.IBlockState state=world.getBlockState(pos);net.minecraft.block.properties.IProperty<Boolean> lit=(net.minecraft.block.properties.IProperty<Boolean>)CANDLE.getField("LIT").get(null);if(!state.getValue(lit))return false;world.setBlockState(pos,state.withProperty(lit,false),3);ItemSounds.at(world,x+.5,y+.5,z+.5,"block.candle.extinguish",1,1);return true;}
  }catch(ReflectiveOperationException failure){throw new IllegalStateException(failure);}return false;
 }
}
