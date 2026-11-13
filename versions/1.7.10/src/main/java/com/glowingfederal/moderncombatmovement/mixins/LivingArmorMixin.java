package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.combat.ArmorRules;
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class LivingArmorMixin {
 @Unique private float mcm$armorInput;
 @Inject(method={"applyArmorCalculations","func_70655_b"},at=@At("HEAD"))private void mcm$beforeArmor(DamageSource source,float damage,CallbackInfoReturnable<Float> ci){mcm$armorInput=damage;}
 @Inject(method={"applyArmorCalculations","func_70655_b"},at=@At("RETURN"),cancellable=true)private void mcm$afterArmor(DamageSource source,float damage,CallbackInfoReturnable<Float> ci){float value=ArmorRules.reduce((EntityLivingBase)(Object)this,source,mcm$armorInput);if(!Float.isNaN(value))ci.setReturnValue(value);}
}
