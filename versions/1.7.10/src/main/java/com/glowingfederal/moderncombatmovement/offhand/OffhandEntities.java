package com.glowingfederal.moderncombatmovement.offhand;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
/** Scoped native interaction hand, never a global held-item override or selected-slot replacement. */
public final class OffhandEntities {
 public interface Adapter { boolean interact(ItemStack stack,EntityPlayer player,Entity target); }
 private static final java.util.Map<Item,Adapter> ADAPTERS=new java.util.IdentityHashMap<Item,Adapter>();
 private static final ThreadLocal<EntityPlayer> CURRENT=new ThreadLocal<EntityPlayer>();
 private OffhandEntities(){}
 public static void register(Item item,Adapter adapter){if(item==null||adapter==null||ADAPTERS.containsKey(item))throw new IllegalArgumentException("Invalid or duplicate entity-use adapter");ADAPTERS.put(item,adapter);}
 public static ItemStack stack(InventoryPlayer inventory){EntityPlayer p=CURRENT.get();return p!=null&&p.inventory==inventory?Offhands.get(p):inventory.getCurrentItem();}
 public static void set(InventoryPlayer inventory,int slot,ItemStack stack){EntityPlayer p=CURRENT.get();if(p!=null&&p.inventory==inventory&&slot==inventory.currentItem)Offhands.set(p,stack);else inventory.setInventorySlotContents(slot,stack);}
 public static ItemStack take(InventoryPlayer inventory,int slot,int count){EntityPlayer p=CURRENT.get();return p!=null&&p.inventory==inventory&&slot==inventory.currentItem?Offhands.inventory(p).decrStackSize(0,count):inventory.decrStackSize(slot,count);}
 public static ItemStack held(EntityPlayer p){return CURRENT.get()==p?Offhands.get(p):p.getHeldItem();}
 public static boolean interact(EntityPlayer p,Entity target){
  ItemStack stack=Offhands.get(p);if(stack==null||target==p||target.isDead||!p.canEntityBeSeen(target)||p.getDistanceSqToEntity(target)>(p.capabilities.isCreativeMode?25:9))return false;
  Adapter adapter=ADAPTERS.get(stack.getItem());
  if(!OffhandUses.supported(stack)&&adapter==null)return false;
  if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.EntityInteractEvent(p,target)))return false;
  int count=stack.stackSize,damage=stack.getItemDamage();EntityPlayer previous=CURRENT.get();CURRENT.set(p);
  boolean used=false;
  try{
   if(adapter!=null)used=adapter.interact(stack,p,target);
   else {
    // Foreign entity overrides must opt in; native callbacks inherited by animals retain their original owner.
    if(target.getClass().getName().startsWith("net.minecraft.entity."))used=target.interactFirst(p);
    if(!used&&target instanceof EntityLivingBase)used=stack.interactWithEntity(p,(EntityLivingBase)target);
   }
  }finally{if(previous==null)CURRENT.remove();else CURRENT.set(previous);}
  if(p.capabilities.isCreativeMode){stack.stackSize=count;stack.setItemDamage(damage);Offhands.set(p,stack);}
  else if(Offhands.get(p)!=null&&Offhands.get(p).stackSize<=0)Offhands.set(p,null);
  if(used)Offhands.swing(p);p.inventoryContainer.detectAndSendChanges();return used;
 }
}
