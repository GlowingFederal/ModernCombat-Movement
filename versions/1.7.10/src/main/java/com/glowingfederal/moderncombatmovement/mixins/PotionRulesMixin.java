package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Preserve native caches and saved metadata; only the native potion's local effect lookup uses the modern full duration. */
@Mixin(value=ItemPotion.class,remap=false)
public abstract class PotionRulesMixin {
 @Group(name="mcmSplashDuration",min=1) @Redirect(method={"getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;","func_77832_l(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"},at=@At(value="INVOKE",target="Lnet/minecraft/item/ItemStack;getItemDamage()I"),require=0,expect=0)
 private int mcm$splashMcp(ItemStack stack){return mcm$metadata(stack);}
 @Group(name="mcmSplashDuration",min=1) @Redirect(method={"getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;","func_77832_l(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"},at=@At(value="INVOKE",target="Lnet/minecraft/item/ItemStack;getMetadata()I"),require=0,expect=0)
 private int mcm$splashMapped(ItemStack stack){return mcm$metadata(stack);}
 @Group(name="mcmSplashDuration",min=1) @Redirect(method={"getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;","func_77832_l(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"},at=@At(value="INVOKE",target="Lnet/minecraft/item/ItemStack;func_77960_j()I"),require=0,expect=0)
 private int mcm$splashSrg(ItemStack stack){return mcm$metadata(stack);}
 private int mcm$metadata(ItemStack stack){int meta=stack.getItemDamage();return (Object)this==net.minecraft.init.Items.potionitem&&stack.getItem()==net.minecraft.init.Items.potionitem&&com.glowingfederal.moderncombatmovement.config.Settings.kit(cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient())?meta&~16384:meta;}
 @Inject(method={"getItemStackDisplayName","func_77653_i"},at=@At("HEAD"),cancellable=true)private void mcm$name(ItemStack stack,CallbackInfoReturnable<String> ci){String name=com.glowingfederal.moderncombatmovement.compat.PotionDelivery.displayName(stack);if(name!=null)ci.setReturnValue(name);}
}
