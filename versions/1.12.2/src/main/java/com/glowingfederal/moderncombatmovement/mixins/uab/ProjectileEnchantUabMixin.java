package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets={"ganymedes01.etfuturum.ModEnchantments$TridentEnchantment","ganymedes01.etfuturum.ModEnchantments$CrossbowEnchantment"},remap=false)
public abstract class ProjectileEnchantUabMixin {
 @Inject(method={"getMinEnchantability","func_77321_a"},at=@At("HEAD"),cancellable=true)private void mcm$minimum(int level,CallbackInfoReturnable<Integer> ci){int cost=com.glowingfederal.moderncombatmovement.compat.ProjectileEnchantments.minimum((Enchantment)(Object)this,level);if(cost>=0)ci.setReturnValue(cost);}
 @Inject(method={"getMaxEnchantability","func_77317_b"},at=@At("HEAD"),cancellable=true)private void mcm$maximum(int level,CallbackInfoReturnable<Integer> ci){int cost=com.glowingfederal.moderncombatmovement.compat.ProjectileEnchantments.maximum((Enchantment)(Object)this,level);if(cost>=0)ci.setReturnValue(cost);}
}
