package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemCrossbow",remap=false)
public abstract class CrossbowIconsUabMixin {
 @Inject(method="getIcon(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/util/IIcon;",at=@At("HEAD"),cancellable=true)private void mcm$icon(ItemStack stack,int pass,CallbackInfoReturnable<IIcon> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.projectiles(true))ci.setReturnValue(com.glowingfederal.moderncombatmovement.render.CrossbowIcons.icon(stack,null));}
 @Inject(method="getIcon(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/util/IIcon;",at=@At("HEAD"),cancellable=true)private void mcm$held(ItemStack stack,int pass,EntityPlayer player,ItemStack using,int remaining,CallbackInfoReturnable<IIcon> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.projectiles(true))ci.setReturnValue(com.glowingfederal.moderncombatmovement.render.CrossbowIcons.icon(stack,player));}
}
