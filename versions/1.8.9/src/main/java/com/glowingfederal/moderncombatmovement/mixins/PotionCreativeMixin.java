package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.item.*;
import net.minecraft.creativetab.CreativeTabs;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ItemPotion.class,remap=false)
public abstract class PotionCreativeMixin {
 @Inject(method={"getSubItems","func_150895_a"},at=@At("RETURN")) private void mcm$potions(Item item,CreativeTabs tab,List<ItemStack> list,CallbackInfo ci) { com.glowingfederal.moderncombatmovement.compat.KitEffects.addPotions(item,list); }
}
