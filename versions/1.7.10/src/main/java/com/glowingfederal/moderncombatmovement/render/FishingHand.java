package com.glowingfederal.moderncombatmovement.render;
public final class FishingHand {
 private FishingHand(){}
 public static boolean off(net.minecraft.entity.player.EntityPlayer player){if(player==null)return false;net.minecraft.item.ItemStack main=player.getCurrentEquippedItem(),off=com.glowingfederal.moderncombatmovement.offhand.Offhands.get(player);return (main==null||main.getItem()!=net.minecraft.init.Items.fishing_rod)&&off!=null&&off.getItem()==net.minecraft.init.Items.fishing_rod;}
}
