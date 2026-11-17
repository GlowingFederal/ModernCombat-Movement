package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Retain saved potion data through UAB's existing conversion; no brewing recipes or stand replacement. */
@Pseudo @Mixin(targets="ganymedes01.etfuturum.tileentities.TileEntityNewBrewingStand",remap=false)
public abstract class LingeringBrewingUabMixin extends net.minecraft.tileentity.TileEntityBrewingStand {
 @Unique private ItemStack[] mcm$bottles;
 @Inject(method="brewPotions",at=@At("HEAD"))private void mcm$capture(CallbackInfo ci){
  mcm$bottles=null;
  if(worldObj==null||worldObj.isRemote||!com.glowingfederal.moderncombatmovement.config.Settings.kit(false))return;
  ItemStack ingredient=getStackInSlot(3);
  if(ingredient==null||ingredient.getItem()!=cpw.mods.fml.common.registry.GameRegistry.findItem("etfuturum","dragon_breath"))return;
  mcm$bottles=new ItemStack[3];for(int i=0;i<3;i++){ItemStack stack=getStackInSlot(i);if(stack!=null&&stack.getItem()==net.minecraft.init.Items.potionitem&&net.minecraft.item.ItemPotion.isSplash(stack.getItemDamage())&&stack.hasTagCompound())mcm$bottles[i]=stack.copy();}
 }
 @Inject(method="brewPotions",at={@At(value="INVOKE",target="Lnet/minecraftforge/event/ForgeEventFactory;onPotionBrewed([Lnet/minecraft/item/ItemStack;)V"),@At("RETURN")})private void mcm$restore(CallbackInfo ci){
  ItemStack[] bottles=mcm$bottles;mcm$bottles=null;if(bottles==null)return;
  for(int i=0;i<3;i++){
   ItemStack before=bottles[i],after=getStackInSlot(i);
   if(before==null||after==null||after.getItem()!=com.glowingfederal.moderncombatmovement.compat.PotionDelivery.lingering||after.getItemDamage()!=before.getItemDamage())continue;
   NBTTagCompound saved=(NBTTagCompound)before.getTagCompound().copy();
   if(after.hasTagCompound())for(Object key:after.getTagCompound().func_150296_c()){String name=(String)key;saved.setTag(name,after.getTagCompound().getTag(name).copy());}
   after.setTagCompound(saved);markDirty();
  }
 }
}
