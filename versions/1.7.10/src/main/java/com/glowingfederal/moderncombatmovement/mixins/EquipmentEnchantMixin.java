package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
import com.glowingfederal.moderncombatmovement.combat.EquipmentEnchantments;
@Mixin(value=Enchantment.class,remap=false)
public abstract class EquipmentEnchantMixin {
 @Inject(method={"canApply","func_92089_a"},at=@At("HEAD"),cancellable=true)private void mcm$equipment(ItemStack stack,CallbackInfoReturnable<Boolean> ci){Enchantment e=(Enchantment)(Object)this;if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.known(stack)){ci.setReturnValue(com.glowingfederal.moderncombatmovement.combat.EquipmentEnchantments.elytraAllowed((net.minecraft.enchantment.Enchantment)(Object)this));return;}if(ModernEquipment.mace(stack))ci.setReturnValue(EquipmentEnchantments.maceAllowed(e));else if(ModernEquipment.spear(stack)!=null)ci.setReturnValue(EquipmentEnchantments.spearAllowed(e));}
}
