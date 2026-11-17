package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=Item.class,remap=false)
public abstract class ItemRepairMixin {
 @Inject(method={"getIsRepairable","func_82789_a"},at=@At("HEAD"),cancellable=true)private void mcm$repair(ItemStack stack,ItemStack ingredient,CallbackInfoReturnable<Boolean> ci){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(false))return;
  Boolean repair=com.glowingfederal.moderncombatmovement.compat.EquipmentItems.repair(stack,ingredient);if(repair!=null)ci.setReturnValue(repair);
 }
}
