package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemArrowTipped",remap=false)
public abstract class TippedArrowUabMixin extends net.minecraft.item.Item {
 @Override public String getItemStackDisplayName(ItemStack stack){String name=com.glowingfederal.moderncombatmovement.compat.Ammunition.displayName(stack);return name==null?super.getItemStackDisplayName(stack):name;}
 @Inject(method="getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;",at=@At("HEAD"),cancellable=true)
 private void mcm$effects(ItemStack stack,CallbackInfoReturnable<java.util.List<net.minecraft.potion.PotionEffect>> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.kit(cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient()))ci.setReturnValue(com.glowingfederal.moderncombatmovement.compat.Ammunition.effects(stack));}
 @Inject(method={"getSubItems","func_150895_a"},at=@At("RETURN"),require=0)private void mcm$creative(net.minecraft.item.Item item,net.minecraft.creativetab.CreativeTabs tab,java.util.List<ItemStack> list,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){com.glowingfederal.moderncombatmovement.compat.Ammunition.addCreative(item,list);}
}
