package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.offhand.*;
@Mixin(value=ContainerPlayer.class,remap=false)
public abstract class OffhandContainerMixin extends Container {
 @Inject(method="<init>",at=@At("RETURN")) private void mcm$slot(InventoryPlayer inv,boolean local,EntityPlayer p,CallbackInfo ci){addSlotToContainer(new Slot(Offhands.inventory(p),0,77,62));}
 @Inject(method={"transferStackInSlot","func_82846_b"},at=@At("HEAD"),cancellable=true) private void mcm$transfer(EntityPlayer p,int index,CallbackInfoReturnable<ItemStack> ci){
  if(index<0||index>=inventorySlots.size())return;Slot slot=(Slot)inventorySlots.get(index);if(slot.inventory!=Offhands.inventory(p))return;
  ItemStack stack=slot.getStack();if(stack==null){ci.setReturnValue(null);return;}ItemStack original=stack.copy();
  // Search actual player slots: inventory extensions may append or relocate their own slots.
  for(int i=inventorySlots.size()-1;i>=0&&stack.stackSize>0;i--){Slot dst=(Slot)inventorySlots.get(i);if(dst.inventory==p.inventory&&dst.getSlotIndex()<36&&dst.isItemValid(stack))mergeItemStack(stack,i,i+1,false);}
  if(stack.stackSize==original.stackSize){ci.setReturnValue(null);return;}
  if(stack.stackSize<=0)slot.putStack(null);else slot.onSlotChanged();slot.onPickupFromSlot(p,stack);ci.setReturnValue(original);
 }
}
