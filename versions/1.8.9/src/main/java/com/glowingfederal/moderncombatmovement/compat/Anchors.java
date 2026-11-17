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
   standalone=new AnchorBlock(); net.minecraftforge.fml.common.registry.GameRegistry.registerBlock(standalone,"respawn_anchor");
  }
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean charge(World w,int x,int y,int z,int count){net.minecraft.util.BlockPos pos=new net.minecraft.util.BlockPos(x,y,z);net.minecraft.block.state.IBlockState state=w.getBlockState(pos);return known(state.getBlock())&&w.setBlockState(pos,state.getBlock().getStateFromMeta(Math.max(0,Math.min(4,count))),3);}
 public static boolean known(Block block) { Object name=Block.blockRegistry.getNameForObject(block); return block==standalone || name!=null && "etfuturum:respawn_anchor".equals(name.toString()); }
 public static boolean preferOffhand(EntityPlayer p,ItemStack held,int charge){ItemStack off=com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p);return charge<4&&com.glowingfederal.moderncombatmovement.config.Settings.kit(p.worldObj.isRemote)&&com.glowingfederal.moderncombatmovement.config.Settings.offhand(p.worldObj.isRemote)&&com.glowingfederal.moderncombatmovement.offhand.Offhands.allowed(p)&&(held==null||held.getItem()!=Item.getItemFromBlock(Blocks.glowstone))&&off!=null&&off.getItem()==Item.getItemFromBlock(Blocks.glowstone);}
 public static boolean use(World w,int x,int y,int z,EntityPlayer p,ItemStack held) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(w.isRemote)) return false;
  Block block=w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)).getBlock(); if(!known(block)) return false;
  int charge=block.getMetaFromState(w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)));
  if(preferOffhand(p,held,charge))return false;
  if(held!=null && held.getItem()==Item.getItemFromBlock(Blocks.glowstone) && charge<4) {
   if(!w.isRemote && charge(w,x,y,z,charge+1)) { if(!p.capabilities.isCreativeMode) --held.stackSize; p.inventory.markDirty(); }
   return true;
  }
  if(charge==0) return false;
  if(w.provider.getHasNoSky() && w.provider.getDimensionId()==-1) {
   if(block!=standalone) return false; // UAB retains its complete native respawn binding lifecycle.
   if(!w.isRemote)AnchorRespawn.bind(p,w,new net.minecraft.util.BlockPos(x,y,z));
   return true;
  }
  if(!w.isRemote) {
   boolean wet=w.getBlockState(new net.minecraft.util.BlockPos(x,y+1,z)).getBlock().getMaterial()==net.minecraft.block.material.Material.water;
   for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) if(w.getBlockState(new net.minecraft.util.BlockPos(x+direction[0],y,z+direction[1])).getBlock().getMaterial()==net.minecraft.block.material.Material.water) wet=true;
   w.setBlockToAir(new net.minecraft.util.BlockPos(x,y,z)); AnchorBlast.explode(w,x,y,z,wet);
  }
  return true;
 }
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.HIGH) public void interact(PlayerInteractEvent e) {
   if(e.action!=PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
   EntityPlayer p=e.entityPlayer; World w=p.worldObj; int x=e.pos.getX(),y=e.pos.getY(),z=e.pos.getZ();
   if(w.isRemote||p.isSneaking()&&(p.getHeldItem()!=null||com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p)!=null))return;
   if(!known(w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)).getBlock())) return;
   if(!(w.isBlockModifiable(p,new net.minecraft.util.BlockPos(x,y,z)) && p.canPlayerEdit(new net.minecraft.util.BlockPos(x,y,z),e.face,p.getHeldItem()))) return;
   ItemStack held=p.getHeldItem();
   if(use(w,x,y,z,p,held)) { e.setCanceled(true); e.useBlock=Event.Result.DENY; e.useItem=Event.Result.DENY; }
  }
 }
}
