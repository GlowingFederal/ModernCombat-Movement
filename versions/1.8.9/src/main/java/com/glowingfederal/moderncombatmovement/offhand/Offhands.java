package com.glowingfederal.moderncombatmovement.offhand;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
/** Native, server-thread entry points shared by containers, packets and equipment consumers. */
public final class Offhands {
 private Offhands(){}
 public static OffhandInventory inventory(EntityPlayer p){return ((OffhandAccess)p).mcm$offhand();}
 public static ItemStack get(EntityPlayer p){return inventory(p).getStackInSlot(0);}
 public static void set(EntityPlayer p,ItemStack stack){inventory(p).setInventorySlotContents(0,stack);}
 public static void swing(EntityPlayer p){OffhandInventory hand=inventory(p);hand.swingTicks=6;hand.swingSerial++;}
 public static boolean using(EntityPlayer p){return ((OffhandAccess)p).mcm$offhandUsing();}
 public static void using(EntityPlayer p,boolean value){((OffhandAccess)p).mcm$offhandUsing(value);}
 public static void read(EntityPlayer p,NBTTagCompound tag){set(p,tag.hasKey("MCMOffhand",10)?ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MCMOffhand")):null);}
 public static void write(EntityPlayer p,NBTTagCompound tag){ItemStack stack=get(p);if(stack!=null&&stack.stackSize>0)tag.setTag("MCMOffhand",stack.writeToNBT(new NBTTagCompound()));else tag.removeTag("MCMOffhand");}
 public static void copy(EntityPlayer p,EntityPlayer old,boolean keep){if(old.isEntityAlive())com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).shieldCooldown=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(old).shieldCooldown;set(p,keep&&get(old)!=null?get(old).copy():null);}
 public static void drop(EntityPlayer p){ItemStack stack=get(p);if(stack!=null){set(p,null);p.dropItem(stack,true,false);}}
 public static boolean allowed(EntityPlayer p){return p.isEntityAlive()&&!p.isPlayerSleeping()&&!p.isSpectator();}
 public static void swap(EntityPlayer p){
  if(!allowed(p)||p.openContainer!=p.inventoryContainer||p.inventory.getItemStack()!=null)return;
  p.stopUsingItem();ItemStack main=p.inventory.getCurrentItem();p.inventory.setInventorySlotContents(p.inventory.currentItem,get(p));set(p,main);p.inventoryContainer.detectAndSendChanges();
 }
 /** Never trust a client stack, slot contents, window owner or requested stack count. */
 public static void swapSlot(EntityPlayer p,int window,int index){
  Container c=p.openContainer;
  if(!allowed(p)||!c.canInteractWith(p)||c.windowId!=window||p.inventory.getItemStack()!=null||index<0||index>=c.inventorySlots.size())return;
  Slot slot=(Slot)c.inventorySlots.get(index);ItemStack hand=get(p),other=slot.getStack();
  if(slot.inventory==inventory(p)||slot instanceof SlotCrafting||!slot.canTakeStack(p)||hand!=null&&(!slot.isItemValid(hand)||hand.stackSize>Math.min(slot.getSlotStackLimit(),hand.getMaxStackSize())))return;
  p.stopUsingItem();slot.putStack(hand);set(p,other);if(other!=null)slot.onPickupFromSlot(p,other);c.detectAndSendChanges();
 }
 /** The legacy creative packet excludes the appended hand slot from its native range. */
 public static boolean creative(EntityPlayerMP p,int index,ItemStack stack){
  Container c=p.inventoryContainer;if(index<0||index>=c.inventorySlots.size()||((Slot)c.inventorySlots.get(index)).inventory!=inventory(p))return false;
  if(!p.theItemInWorldManager.isCreative()||!allowed(p)||stack!=null&&(stack.getItem()==null||stack.getItemDamage()<0||stack.stackSize<=0||stack.stackSize>Math.min(64,stack.getMaxStackSize())))return true;
  set(p,ItemStack.copyItemStack(stack));c.detectAndSendChanges();return true;
 }
}
