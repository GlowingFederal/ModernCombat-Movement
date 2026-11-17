package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.world.World;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
public final class Anchors {
 public static Block standalone;
 private Anchors() {}
 public static void register() {
  if(!Content.uab) {
   standalone=new AnchorBlock(); cpw.mods.fml.common.registry.GameRegistry.registerBlock(standalone,"respawn_anchor");
  }
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean charge(World w,int x,int y,int z,int count){if(!known(w.getBlock(x,y,z))||!w.setBlockMetadataWithNotify(x,y,z,Math.max(0,Math.min(4,count)),3))return false;w.func_147451_t(x,y,z);return true;}
 public static boolean known(Block block) { Object name=Block.blockRegistry.getNameForObject(block); return block==standalone || name!=null && "etfuturum:respawn_anchor".equals(name.toString()); }
 public static boolean preferOffhand(EntityPlayer p,ItemStack held,int charge){ItemStack off=com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p);return charge<4&&com.glowingfederal.moderncombatmovement.config.Settings.kit(p.worldObj.isRemote)&&com.glowingfederal.moderncombatmovement.config.Settings.offhand(p.worldObj.isRemote)&&com.glowingfederal.moderncombatmovement.offhand.Offhands.allowed(p)&&(held==null||held.getItem()!=Item.getItemFromBlock(Blocks.glowstone))&&off!=null&&off.getItem()==Item.getItemFromBlock(Blocks.glowstone);}
 public static boolean use(World w,int x,int y,int z,EntityPlayer p,ItemStack held) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(w.isRemote)) return false;
  Block block=w.getBlock(x,y,z); if(!known(block)) return false;
  int charge=w.getBlockMetadata(x,y,z);
  if(preferOffhand(p,held,charge))return false;
  if(held!=null && held.getItem()==Item.getItemFromBlock(Blocks.glowstone) && charge<4) {
   if(!w.isRemote && charge(w,x,y,z,charge+1)) { if(!p.capabilities.isCreativeMode) --held.stackSize; p.inventory.markDirty(); }
   return true;
  }
  if(charge==0) return false;
  if(w.provider.isHellWorld) {
   if(block!=standalone) return false; // UAB retains its complete native respawn binding lifecycle.
   if(!w.isRemote)AnchorRespawn.bind(p,w,new net.minecraft.util.ChunkCoordinates(x,y,z));
   return true;
  }
  if(!w.isRemote) {
   boolean wet=w.getBlock(x,y+1,z).getMaterial()==net.minecraft.block.material.Material.water;
   for(int[] direction:new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) if(w.getBlock(x+direction[0],y,z+direction[1]).getMaterial()==net.minecraft.block.material.Material.water) wet=true;
   w.setBlockToAir(x,y,z); AnchorBlast.explode(w,x,y,z,wet);
  }
  return true;
 }
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.HIGH) public void interact(PlayerInteractEvent e) {
   if(e.action!=PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
   EntityPlayer p=e.entityPlayer; World w=p.worldObj; int x=e.x,y=e.y,z=e.z;
   if(w.isRemote||p.isSneaking()&&(p.getHeldItem()!=null||com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p)!=null))return;
   if(!known(w.getBlock(x,y,z))) return;
   if(!(w.canMineBlock(p,x,y,z) && p.canPlayerEdit(x,y,z,e.face,p.getHeldItem()))) return;
   ItemStack held=p.getHeldItem();
   if(use(w,x,y,z,p,held)) { e.setCanceled(true); e.useBlock=Event.Result.DENY; e.useItem=Event.Result.DENY; }
  }
 }
}
