package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemLingeringPotion",remap=false)
public abstract class LingeringUabMixin {
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$throw(ItemStack stack,World world,EntityPlayer player,CallbackInfoReturnable<ItemStack> ci){
  if(com.glowingfederal.moderncombatmovement.config.Settings.kit(world.isRemote)&&com.glowingfederal.moderncombatmovement.compat.PotionDelivery.enabled()){
   com.glowingfederal.moderncombatmovement.compat.PotionDelivery.use(stack,world,player);ci.setReturnValue(stack);
  }
 }
 @Inject(method={"getItemStackDisplayName","func_77653_i"},at=@At("HEAD"),cancellable=true)
 private void mcm$name(ItemStack stack,CallbackInfoReturnable<String> ci){String name=com.glowingfederal.moderncombatmovement.compat.PotionDelivery.displayName(stack);if(name!=null)ci.setReturnValue(name);}
 @Inject(method={"getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;","func_77832_l(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"},at=@At("HEAD"),cancellable=true)
 private void mcm$displayEffects(ItemStack stack,CallbackInfoReturnable<java.util.List<net.minecraft.potion.PotionEffect>> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.kit(cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient())&&com.glowingfederal.moderncombatmovement.compat.PotionDelivery.enabled())ci.setReturnValue(com.glowingfederal.moderncombatmovement.compat.PotionDelivery.lingeringEffects(stack));}
}
