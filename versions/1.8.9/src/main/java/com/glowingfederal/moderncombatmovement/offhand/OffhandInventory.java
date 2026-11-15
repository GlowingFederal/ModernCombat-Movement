package com.glowingfederal.moderncombatmovement.offhand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
/** A real one-slot equipment inventory. Slot indices in InventoryPlayer stay unchanged. */
public final class OffhandInventory implements IInventory {
 private final EntityPlayer owner;
 private ItemStack stack;
 public ItemStack lastSent; public boolean lastUsing;
 public int swingTicks,swingSerial,lastSwingSerial;
 public OffhandInventory(EntityPlayer owner){this.owner=owner;}
 public int getSizeInventory(){return 1;}
 public ItemStack getStackInSlot(int index){return index==0?stack:null;}
 public ItemStack decrStackSize(int index,int count){
  if(index!=0||stack==null||count<=0)return null;
  ItemStack result;
  if(count>=stack.stackSize){result=stack;stack=null;}else result=stack.splitStack(count);
  markDirty();return result;
 }
 public ItemStack removeStackFromSlot(int index){ItemStack result=getStackInSlot(index);if(index==0)setInventorySlotContents(0,null);return result;}
 public void setInventorySlotContents(int index,ItemStack value){if(index!=0)throw new IndexOutOfBoundsException();if(owner.worldObj.isRemote&&ItemStack.areItemStacksEqual(stack,value))return;stack=value!=null&&value.stackSize>0?value:null;markDirty();}
 public int getInventoryStackLimit(){return 64;}
 public void markDirty(){if(owner.inventory!=null)owner.inventory.markDirty();}
 public boolean isUseableByPlayer(EntityPlayer p){return p==owner&&!owner.isDead;}
 public boolean isItemValidForSlot(int index,ItemStack value){return index==0;}
 public String getName(){return "container.moderncombatmovement.offhand";}
 public boolean hasCustomName(){return false;}
 public net.minecraft.util.IChatComponent getDisplayName(){return new net.minecraft.util.ChatComponentTranslation(getName());}
 public void openInventory(EntityPlayer p){} public void closeInventory(EntityPlayer p){}
 public int getField(int id){return 0;} public void setField(int id,int value){} public int getFieldCount(){return 0;}
 public void clear(){setInventorySlotContents(0,null);}
}
