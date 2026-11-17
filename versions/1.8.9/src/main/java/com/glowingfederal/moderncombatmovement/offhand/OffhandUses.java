package com.glowingfederal.moderncombatmovement.offhand;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
import com.glowingfederal.moderncombatmovement.compat.*;
import com.glowingfederal.moderncombatmovement.network.Packets;
/** Use callbacks receive the actual offhand stack. No temporary hotbar substitution or global held-item override. */
public final class OffhandUses {
 public interface Adapter {
  ItemStack use(ItemStack stack,World world,EntityPlayer player);
  boolean useOnBlock(ItemStack stack,EntityPlayer player,int x,int y,int z,int side,float hitX,float hitY,float hitZ);
 }
 private static final Map<Item,Adapter> ADAPTERS=new IdentityHashMap<Item,Adapter>();
 private OffhandUses(){}
 /** Register during mod initialization. Foreign code that assumes the selected hotbar needs an explicit hand-aware adapter. */
 public static void register(Item item,Adapter adapter){if(item==null||adapter==null)throw new IllegalArgumentException();if(ADAPTERS.containsKey(item))throw new IllegalStateException("Duplicate offhand adapter");ADAPTERS.put(item,adapter);}
 public static boolean supported(ItemStack s){return s!=null&&(CombatHooks.id(s).startsWith("minecraft:")||CombatHooks.id(s).startsWith("moderncombatmovement:")||Crossbows.known(s)||ModernEquipment.spear(s)!=null||ADAPTERS.containsKey(s.getItem()));}
 public static boolean useAir(EntityPlayer p){
  ItemStack s=Offhands.get(p);if(!supported(s))return false;
  ItemStack result;int before=s.stackSize,damage=s.getItemDamage();
  if(s.getItem() instanceof ItemArmor){
   ItemArmor armor=(ItemArmor)s.getItem();int slot=3-armor.armorType;if(p.inventory.armorInventory[slot]!=null)return false;
   ItemStack equipped=s.copy();equipped.stackSize=1;p.inventory.armorInventory[slot]=equipped;if(!p.capabilities.isCreativeMode)--s.stackSize;result=s;
  }else{
   Adapter adapter=ADAPTERS.get(s.getItem());result=adapter==null?s.useItemRightClick(p.worldObj,p):adapter.use(s,p.worldObj,p);
  }
  Offhands.set(p,result);return p.isUsingItem()||result!=s||s.stackSize!=before||s.getItemDamage()!=damage;
 }
 public static void handle(EntityPlayerMP p,Packets.OffhandUse request){
  if(request.playerId!=p.getEntityId()||request.dimension!=p.dimension)return;

  com.glowingfederal.moderncombatmovement.pose.PlayerState state=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  if(request.serial!=0){if(!com.glowingfederal.moderncombatmovement.pose.UseSequence.newer(request.serial,state.offhandAckSerial))return;state.offhandAckSerial=request.serial;state.lastSent=-1;}

  if(request.release){if(Offhands.using(p))p.stopUsingItem();return;}
  if(!com.glowingfederal.moderncombatmovement.config.Settings.offhand(false)||!Offhands.allowed(p)||p.openContainer!=p.inventoryContainer||p.inventory.getItemStack()!=null||p.isUsingItem()||p.inventory.currentItem!=request.selected)return;
  ItemStack s=Offhands.get(p);if(!supported(s))return;
  if(request.entity>=0){net.minecraft.entity.Entity target=p.worldObj.getEntityByID(request.entity);if(target!=null&&!OffhandEntities.interact(p,target))useAir(p);return;}
  if(request.side>=0){
   double reach=p.theItemInWorldManager.getBlockReachDistance();
   Vec3 eye=new Vec3(p.posX,p.getEntityBoundingBox().minY+p.getEyeHeight(),p.posZ),look=p.getLookVec();
   MovingObjectPosition hit=p.worldObj.rayTraceBlocks(eye,eye.addVector(look.xCoord*reach,look.yCoord*reach,look.zCoord*reach));
   if(hit==null||!hit.getBlockPos().equals(new BlockPos(request.x,request.y,request.z))||hit.sideHit.getIndex()!=request.side)return;
   int x=request.x,y=request.y,z=request.z;
   if(!p.canPlayerEdit(new BlockPos(x,y,z),EnumFacing.getFront(request.side),s)||!p.worldObj.isBlockModifiable(p,new BlockPos(x,y,z)))return;
   net.minecraftforge.event.entity.player.PlayerInteractEvent event=net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(p,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK,p.worldObj,new BlockPos(x,y,z),EnumFacing.getFront(request.side));
   if(event.isCanceled()||event.useItem==net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)return;
   Adapter adapter=ADAPTERS.get(s.getItem());
   int count=s.stackSize,damage=s.getItemDamage();boolean used;
   if(adapter!=null)used=adapter.useOnBlock(s,p,x,y,z,request.side,request.hx,request.hy,request.hz);
   else if(!p.isSneaking()&&event.useBlock!=net.minecraftforge.fml.common.eventhandler.Event.Result.DENY&&Anchors.use(p.worldObj,x,y,z,p,s))used=true;
   else used=s.onItemUse(p,p.worldObj,new BlockPos(x,y,z),EnumFacing.getFront(request.side),request.hx,request.hy,request.hz);
   if(p.capabilities.isCreativeMode){s.stackSize=count;s.setItemDamage(damage);}
   if(s.stackSize<=0)Offhands.set(p,null);
   if(used){Offhands.swing(p);p.inventory.markDirty();return;}
  }else{
   net.minecraftforge.event.entity.player.PlayerInteractEvent event=net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(p,net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR,p.worldObj,null,null);
   if(event.isCanceled()||event.useItem==net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)return;
  }
  useAir(p);p.inventoryContainer.detectAndSendChanges();
 }
}
