package com.glowingfederal.moderncombatmovement.compat;
/** Optional, exact UAB block boundary. No duplicate block/tile registrations or replacement block engine. */
public final class UabCombatBlocks {
 private static Class<?> ROD,BELL,CANDLE;private static java.lang.reflect.Method STRIKE,RING;
 static{if(Content.uab)try{ROD=Class.forName("ganymedes01.etfuturum.blocks.BlockLightningRod");BELL=Class.forName("ganymedes01.etfuturum.blocks.BlockBell");CANDLE=Class.forName("ganymedes01.etfuturum.blocks.BlockStructureDecoration");STRIKE=ROD.getMethod("strike",net.minecraft.world.World.class,int.class,int.class,int.class);RING=BELL.getMethod("ring",net.minecraft.world.World.class,int.class,int.class,int.class);}catch(ReflectiveOperationException failure){throw new IllegalStateException("Unsupported UAB block integration",failure);}}
 private UabCombatBlocks(){}
 public static boolean rod(net.minecraft.world.World world,int x,int y,int z){return ROD!=null&&ROD.isInstance(world.getBlock(x,y,z));}
 public static void strike(net.minecraft.world.World world,int x,int y,int z){try{STRIKE.invoke(world.getBlock(x,y,z),world,x,y,z);}catch(ReflectiveOperationException failure){throw new IllegalStateException(failure);}}
 public static boolean wind(net.minecraft.world.World world,int x,int y,int z){
  if(!Content.uab)return false;net.minecraft.block.Block block=world.getBlock(x,y,z);
  try{if(BELL!=null&&BELL.isInstance(block)){RING.invoke(null,world,x,y,z);return true;}
   if("etfuturum:candle".equals(String.valueOf(net.minecraft.block.Block.blockRegistry.getNameForObject(block)))){int meta=world.getBlockMetadata(x,y,z);if((meta&4)==0)return false;world.setBlockMetadataWithNotify(x,y,z,meta&~4,3);ItemSounds.at(world,x+.5,y+.5,z+.5,"block.candle.extinguish",1,1);return true;}
  }catch(ReflectiveOperationException failure){throw new IllegalStateException(failure);}return false;
 }
}
