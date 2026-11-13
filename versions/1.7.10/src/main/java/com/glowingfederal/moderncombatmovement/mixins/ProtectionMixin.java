package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.combat.ArmorRules;
@Mixin(value=EnchantmentHelper.class,remap=false)
public abstract class ProtectionMixin {
 @Inject(method={"getEnchantmentModifierDamage","func_77508_a"},at=@At("HEAD"),cancellable=true)private static void mcm$epf(ItemStack[] armor,DamageSource source,CallbackInfoReturnable<Integer> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.combat(false)&&ArmorRules.managed(source)&&ArmorRules.supported(armor))ci.setReturnValue(ArmorRules.protection(armor,source));}
}
