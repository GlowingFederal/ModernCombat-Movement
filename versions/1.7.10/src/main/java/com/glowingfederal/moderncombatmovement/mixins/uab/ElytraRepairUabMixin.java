package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.equipment.ItemArmorElytra",remap=false)
public abstract class ElytraRepairUabMixin {
 @Inject(method={"getIsRepairable","func_82789_a"},at=@At("HEAD"),cancellable=true)private void mcm$repair(ItemStack stack,ItemStack ingredient,CallbackInfoReturnable<Boolean> ci){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(false))return;
  ci.setReturnValue(com.glowingfederal.moderncombatmovement.compat.EquipmentItems.membrane(ingredient));
 }
}
