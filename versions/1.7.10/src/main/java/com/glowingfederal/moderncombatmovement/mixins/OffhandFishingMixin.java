package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.offhand.Offhands;
@Mixin(value=EntityFishHook.class,remap=false)
public abstract class OffhandFishingMixin {
 @Unique private ItemStack mcm$rod(EntityPlayer p){ItemStack main=p.getCurrentEquippedItem();return main!=null&&main.getItem()==net.minecraft.init.Items.fishing_rod?main:Offhands.get(p);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;getCurrentEquippedItem()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$held(EntityPlayer p){return mcm$rod(p);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;func_71045_bC()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$heldSrg(EntityPlayer p){return mcm$rod(p);}

 @Redirect(method={"onUpdate","func_70071_h_","func_146033_f"},at=@At(value="INVOKE",target="Lnet/minecraft/enchantment/EnchantmentHelper;func_151386_g(Lnet/minecraft/entity/EntityLivingBase;)I"),require=0)private int mcm$luck(net.minecraft.entity.EntityLivingBase p){return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(61,mcm$rod((EntityPlayer)p));}
 @Redirect(method={"onUpdate","func_70071_h_","func_146033_f"},at=@At(value="INVOKE",target="Lnet/minecraft/enchantment/EnchantmentHelper;func_151387_h(Lnet/minecraft/entity/EntityLivingBase;)I"),require=0)private int mcm$lure(net.minecraft.entity.EntityLivingBase p){return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(62,mcm$rod((EntityPlayer)p));}
}
