package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.DimensionManager;
public final class AnchorRespawn {
 private AnchorRespawn() {}
 public static void init() { AnchorRespawn events=new AnchorRespawn(); net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(events); net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(events); }
 public static NBTTagCompound saved(EntityPlayer p) { return p.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag("MCMAnchor"); }
 public static void clearForBed(EntityPlayer p) {
  NBTTagCompound tag=saved(p);
  if(!tag.getBoolean("respawning")) p.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).removeTag("MCMAnchor");
 }
 @SubscribeEvent(priority=EventPriority.LOWEST) public void death(LivingDeathEvent e) {
  if(e.entityLiving instanceof EntityPlayer && !e.entityLiving.worldObj.isRemote && !e.isCanceled()) { NBTTagCompound tag=saved((EntityPlayer)e.entityLiving); if(tag.hasKey("dimension")) tag.setBoolean("respawning",true); }
 }
 @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent e) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(false) || !(e.player instanceof EntityPlayerMP)) return;
  final EntityPlayerMP p=(EntityPlayerMP)e.player; NBTTagCompound tag=saved(p);
  if(!tag.getBoolean("respawning")) return;
  tag.setBoolean("respawning",false);
  int dimension=tag.getInteger("dimension");
  if(!DimensionManager.isDimensionRegistered(dimension)) return;
  WorldServer w=DimensionManager.getWorld(dimension); if(w==null) { DimensionManager.initDimension(dimension); w=DimensionManager.getWorld(dimension); }
  if(w==null) return;
  int x=tag.getInteger("x"),y=tag.getInteger("y"),z=tag.getInteger("z");
  net.minecraft.block.Block block=w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)).getBlock();
  if(block!=Anchors.standalone) return;
  int charges=block.getMetaFromState(w.getBlockState(new net.minecraft.util.BlockPos(x,y,z))); if(charges<=0) return;
  int[] exit=null;
  for(int dy:new int[]{0,1,-1}) for(int[] d:new int[][]{{0,-1},{-1,0},{1,0},{0,1},{-1,-1},{1,-1},{-1,1},{1,1}}) {
   int ex=x+d[0],ey=y+dy,ez=z+d[1];
   if(exit==null && w.isAirBlock(new net.minecraft.util.BlockPos(ex,ey,ez)) && w.isAirBlock(new net.minecraft.util.BlockPos(ex,ey+1,ez)) && w.isSideSolid(new net.minecraft.util.BlockPos(ex,ey-1,ez),net.minecraft.util.EnumFacing.UP)) exit=new int[]{ex,ey,ez};
  }
  if(exit==null) return;
  final double ex=exit[0]+.5,ey=exit[1],ez=exit[2]+.5;
  if(p.dimension!=dimension) {
   final WorldServer destination=w;
   net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(p,dimension,new Teleporter(destination) {
    @Override public void placeInPortal(Entity entity,float yaw) { entity.setPosition(ex,ey,ez); }
   });
  }
  p.playerNetServerHandler.setPlayerLocation(ex,ey,ez,p.rotationYaw,p.rotationPitch);
  w.setBlockState(new net.minecraft.util.BlockPos(x,y,z),block.getStateFromMeta(charges-1),3);
 }
}
