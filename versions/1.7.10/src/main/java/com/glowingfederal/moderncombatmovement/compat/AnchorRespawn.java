package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.DimensionManager;
public final class AnchorRespawn {
 private AnchorRespawn() {}
 public static void init() { AnchorRespawn events=new AnchorRespawn(); cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(events); net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(events); }
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
  net.minecraft.block.Block block=w.getBlock(x,y,z);
  if(block!=Anchors.standalone) return;
  int charges=w.getBlockMetadata(x,y,z); if(charges<=0) return;
  int[] exit=null;
  for(int dy:new int[]{0,1,-1}) for(int[] d:new int[][]{{0,-1},{-1,0},{1,0},{0,1},{-1,-1},{1,-1},{-1,1},{1,1}}) {
   int ex=x+d[0],ey=y+dy,ez=z+d[1];
   if(exit==null && w.isAirBlock(ex,ey,ez) && w.isAirBlock(ex,ey+1,ez) && w.isSideSolid(ex,ey-1,ez,net.minecraftforge.common.util.ForgeDirection.UP)) exit=new int[]{ex,ey,ez};
  }
  if(exit==null) return;
  final double ex=exit[0]+.5,ey=exit[1],ez=exit[2]+.5;
  if(p.dimension!=dimension) {
   final WorldServer destination=w;
   cpw.mods.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(p,dimension,new Teleporter(destination) {
    @Override public void placeInPortal(Entity entity,double px,double py,double pz,float yaw) { entity.setPosition(ex,ey,ez); }
   });
  }
  p.playerNetServerHandler.setPlayerLocation(ex,ey,ez,p.rotationYaw,p.rotationPitch);
  w.setBlockMetadataWithNotify(x,y,z,charges-1,3);
 }
}
