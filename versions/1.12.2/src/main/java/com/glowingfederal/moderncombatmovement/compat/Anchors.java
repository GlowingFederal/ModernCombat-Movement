package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.world.World;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
public final class Anchors {
 public static Block standalone;
 private Anchors() {}
 public static void register() {
  if(!Content.uab) {
   standalone=new AnchorBlock(); standalone.setRegistryName("moderncombatmovement","respawn_anchor"); net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Block.class).register(standalone); net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Item.class).register(new ItemBlock(standalone).setRegistryName(standalone.getRegistryName()));
  }
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
  AnchorRespawn.init();
 }
 public static boolean known(Block block) { Object name=Block.REGISTRY.getNameForObject(block); return block==standalone || name!=null && "etfuturum:respawn_anchor".equals(name.toString()); }
 public static boolean use(World w,int x,int y,int z,EntityPlayer p,ItemStack held) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(w.isRemote)) return false;
  Block block=w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock(); if(!known(block)) return false;
  int charge=block.getMetaFromState(w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)));
  if(held!=null && !held.isEmpty() && held.getItem()==Item.getItemFromBlock(Blocks.GLOWSTONE) && charge<4) {
   if(!w.isRemote) { w.setBlockState(new net.minecraft.util.math.BlockPos(x,y,z),block.getStateFromMeta(charge+1),3); if(!p.capabilities.isCreativeMode) held.shrink(1); p.inventory.markDirty(); }
   return true;
  }
  if(charge==0) return false;
  if(w.provider.isNether()) {
   if(block!=standalone) return false; // UAB retains its complete native respawn binding lifecycle.
   if(!w.isRemote) { NBTTagCompound persisted=p.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG); NBTTagCompound anchor=new NBTTagCompound(); anchor.setInteger("dimension",p.dimension); anchor.setInteger("x",x);anchor.setInteger("y",y);anchor.setInteger("z",z);persisted.setTag("MCMAnchor",anchor);p.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG,persisted); }
   return true;
  }
  if(!w.isRemote) {
   boolean wet=w.getBlockState(new net.minecraft.util.math.BlockPos(x,y+1,z)).getBlock().getMaterial(w.getBlockState(new net.minecraft.util.math.BlockPos(x,y+1,z)))==net.minecraft.block.material.Material.WATER;
   for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) if(w.getBlockState(new net.minecraft.util.math.BlockPos(x+direction[0],y,z+direction[1])).getBlock().getMaterial(w.getBlockState(new net.minecraft.util.math.BlockPos(x+direction[0],y,z+direction[1])))==net.minecraft.block.material.Material.WATER) wet=true;
   w.setBlockToAir(new net.minecraft.util.math.BlockPos(x,y,z)); AnchorBlast.explode(w,x,y,z,wet);
  }
  return true;
 }
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.HIGH) public void interact(PlayerInteractEvent.RightClickBlock e) {

   EntityPlayer p=e.getEntityPlayer(); World w=p.world; int x=e.getPos().getX(),y=e.getPos().getY(),z=e.getPos().getZ();
   if(!known(w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock())) return;
   if(!(w.isBlockModifiable(p,new net.minecraft.util.math.BlockPos(x,y,z)) && p.canPlayerEdit(new net.minecraft.util.math.BlockPos(x,y,z),e.getFace(),p.getHeldItem(e.getHand())))) return;
   ItemStack held=p.getHeldItem(e.getHand());
   int charges=w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock().getMetaFromState(w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)));
   if(e.getHand()==net.minecraft.util.EnumHand.MAIN_HAND && held.getItem()!=Item.getItemFromBlock(Blocks.GLOWSTONE) && p.getHeldItemOffhand().getItem()==Item.getItemFromBlock(Blocks.GLOWSTONE) && charges<4) return;
   if(use(w,x,y,z,p,held)) { e.setCanceled(true); e.setCancellationResult(net.minecraft.util.EnumActionResult.SUCCESS); }
  }
 }
}
