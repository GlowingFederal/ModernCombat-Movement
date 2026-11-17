package com.glowingfederal.moderncombatmovement.network;

import com.glowingfederal.moderncombatmovement.compat.*;
import com.glowingfederal.moderncombatmovement.combat.EquipmentEnchantments;
/** Compare the live IDs after Forge has completed registration/remapping. */
public final class RegistryAgreement {
 private RegistryAgreement(){}
 public static String signature(){
  StringBuilder value=new StringBuilder("mcm7;uab=").append(Content.uab);
  net.minecraft.enchantment.Enchantment[] enchants={Crossbows.quickCharge,Crossbows.multishot,Crossbows.piercing,Tridents.loyalty,Tridents.impaling,Tridents.riptide,Tridents.channeling,EquipmentEnchantments.density,EquipmentEnchantments.breach,EquipmentEnchantments.windBurst,EquipmentEnchantments.lunge,EquipmentEnchantments.mending,EquipmentEnchantments.sweeping};
  for(net.minecraft.enchantment.Enchantment e:enchants)value.append(';').append(e==null?-1:net.minecraft.enchantment.Enchantment.getEnchantmentID(e));
  for(net.minecraft.potion.Potion p:KitEffects.EFFECTS)value.append(';').append(p==null?-1:net.minecraft.potion.Potion.getIdFromPotion(p));
  return value.toString();
 }
}
