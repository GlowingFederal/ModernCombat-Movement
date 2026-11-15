package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.offhand.*;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class OffhandPlayerMixin {
 @Shadow(aliases="field_71074_e") private ItemStack itemInUse;
 @Shadow(aliases="field_71072_f") private int itemInUseCount;
 @Shadow(aliases="func_71010_c") protected abstract void updateItemUse(ItemStack stack,int count);
 @Inject(method={"readEntityFromNBT","func_70037_a"},at=@At("RETURN")) private void mcm$read(NBTTagCompound tag,CallbackInfo ci){Offhands.read((EntityPlayer)(Object)this,tag);}
 @Inject(method={"writeEntityToNBT","func_70014_b"},at=@At("RETURN")) private void mcm$write(NBTTagCompound tag,CallbackInfo ci){Offhands.write((EntityPlayer)(Object)this,tag);}
 @Inject(method={"clonePlayer","func_71049_a"},at=@At("RETURN")) private void mcm$copy(EntityPlayer old,boolean all,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;Offhands.copy(p,old,all||p.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"));}
 @Inject(method={"setItemInUse","func_71008_a"},at=@At("RETURN")) private void mcm$use(ItemStack stack,int duration,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;Offhands.using(p,itemInUse!=null&&itemInUse==Offhands.get(p));}
 @Inject(method={"clearItemInUse","func_71041_bz"},at=@At("RETURN")) private void mcm$clear(CallbackInfo ci){Offhands.using((EntityPlayer)(Object)this,false);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"),require=0) private ItemStack mcm$tickMcp(InventoryPlayer inv){return mcm$current(inv);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;func_70448_g()Lnet/minecraft/item/ItemStack;"),require=0) private ItemStack mcm$tickSrg(InventoryPlayer inv){return mcm$current(inv);}
 @Unique private ItemStack mcm$current(InventoryPlayer inv){EntityPlayer p=(EntityPlayer)(Object)this;return Offhands.using(p)?Offhands.get(p):inv.getCurrentItem();}
 /** Vanilla's finish callback writes the selected hotbar entry. Preserve its Forge hooks, but write the active hand. */
 @Inject(method={"onItemUseFinish","func_71036_o"},at=@At("HEAD"),cancellable=true) private void mcm$finish(CallbackInfo ci){
  EntityPlayer p=(EntityPlayer)(Object)this;if(!Offhands.using(p))return;
  ci.cancel();if(itemInUse==null||itemInUse!=Offhands.get(p)){p.clearItemInUse();return;}
  updateItemUse(itemInUse,16);
  ItemStack result=itemInUse.onFoodEaten(p.worldObj,p);
  result=net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(p,itemInUse,itemInUseCount,result);
  Offhands.set(p,result);p.clearItemInUse();
 }
}
