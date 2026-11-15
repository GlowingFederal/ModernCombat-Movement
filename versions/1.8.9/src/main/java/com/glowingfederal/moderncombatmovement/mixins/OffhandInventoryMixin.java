package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.offhand.*;
@Mixin(value=InventoryPlayer.class,remap=false)
public abstract class OffhandInventoryMixin {
 @Unique private EntityPlayer mcm$owner(){return ((InventoryPlayer)(Object)this).player;}
 @Inject(method={"dropAllItems","func_70436_m"},at=@At("RETURN")) private void mcm$drop(CallbackInfo ci){Offhands.drop(mcm$owner());}
 @Inject(method={"clearMatchingItems","func_174925_a"},at=@At("RETURN"),cancellable=true) private void mcm$clear(Item item,int meta,int count,net.minecraft.nbt.NBTTagCompound tag,CallbackInfoReturnable<Integer> ci){
 ItemStack s=Offhands.get(mcm$owner());int removed=ci.getReturnValue();if(s==null||item!=null&&s.getItem()!=item||meta>=0&&s.getMetadata()!=meta||tag!=null&&!net.minecraft.nbt.NBTUtil.func_181123_a(tag,s.getTagCompound(),true))return;
 int n=count<=0?s.stackSize:Math.min(Math.max(0,count-removed),s.stackSize);if(count!=0){s.stackSize-=n;if(s.stackSize<=0)Offhands.set(mcm$owner(),null);}ci.setReturnValue(removed+n);
 }
 @Inject(method={"clear","func_174888_l"},at=@At("RETURN")) private void mcm$clearAll(CallbackInfo ci){Offhands.set(mcm$owner(),null);}
}
