package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.combat.*;
@Mixin(value=net.minecraftforge.common.ISpecialArmor.ArmorProperties.class,remap=false)
public abstract class ForgeArmorMixin {
 @Inject(method="ApplyArmor",at=@At("HEAD"),cancellable=true)private static void mcm$nativeArmor(EntityLivingBase entity,ItemStack[] inventory,DamageSource source,double incoming,CallbackInfoReturnable<Float> ci){
  if(!(entity instanceof EntityPlayer)||inventory!=((EntityPlayer)entity).inventory.armorInventory)return;
  float modern=ArmorRules.reduce(entity,source,(float)incoming);if(Float.isNaN(modern))return;
  ((EntityPlayer)entity).inventory.damageArmor((float)incoming);
  ci.setReturnValue(modern);
 }
}
