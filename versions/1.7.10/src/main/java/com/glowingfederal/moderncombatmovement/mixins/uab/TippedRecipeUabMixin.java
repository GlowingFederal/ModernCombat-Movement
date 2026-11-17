package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.recipes.crafting.RecipeTippedArrow",remap=false)
public abstract class TippedRecipeUabMixin {
 @Inject(method={"getCraftingResult","func_77572_b"},at=@At("RETURN"))private void mcm$name(InventoryCrafting grid,CallbackInfoReturnable<ItemStack> ci){ItemStack result=ci.getReturnValue(),potion=grid.getStackInRowAndColumn(1,1);if(result!=null&&potion!=null&&potion.hasTagCompound()&&potion.getTagCompound().hasKey("MCMPotion",8)){if(!result.hasTagCompound())result.setTagCompound(new net.minecraft.nbt.NBTTagCompound());result.getTagCompound().setString("MCMPotion",potion.getTagCompound().getString("MCMPotion"));}}
}
