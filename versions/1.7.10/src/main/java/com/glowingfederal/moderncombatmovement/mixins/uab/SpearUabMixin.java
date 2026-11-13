package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.combat.SpearMechanics;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.items.ItemSpear",remap=false)
public abstract class SpearUabMixin {
 @Inject(method={"getItemUseAction","func_77661_b"},at=@At("HEAD"),cancellable=true)private void mcm$action(ItemStack s,CallbackInfoReturnable<EnumAction> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.kit(false))ci.setReturnValue(EnumAction.none);}
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$use(ItemStack s,World w,EntityPlayer p,CallbackInfoReturnable<ItemStack> ci){if(ModernEquipment.enabled(p)){SpearMechanics.use(s,p);ci.setReturnValue(s);}}
 @Inject(method="onUsingTick",at=@At("HEAD"),cancellable=true)private void mcm$charge(ItemStack s,EntityPlayer e,int remaining,CallbackInfo ci){if(e instanceof EntityPlayer&&ModernEquipment.enabled((EntityPlayer)e)){SpearMechanics.charge(s,(EntityPlayer)e,remaining);ci.cancel();}}
 @Inject(method="onLeftClickEntity",at=@At("HEAD"),cancellable=true)private void mcm$jab(ItemStack s,EntityPlayer p,Entity e,CallbackInfoReturnable<Boolean> ci){if(SpearMechanics.owned(p))ci.setReturnValue(false);}
}
