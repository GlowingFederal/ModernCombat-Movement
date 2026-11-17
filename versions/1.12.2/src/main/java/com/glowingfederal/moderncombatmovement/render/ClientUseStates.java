package com.glowingfederal.moderncombatmovement.render;
/** Local release owns prediction; stale metadata cannot restart a released MCM item use. Native release packets retain server authority. */
public final class ClientUseStates {
 private ClientUseStates(){}
 public static void released(net.minecraft.entity.EntityLivingBase entity){
  if(entity.world==null||!entity.world.isRemote)return;
  net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.getMinecraft();if(entity!=mc.player||!entity.isHandActive())return;
  net.minecraft.item.ItemStack stack=entity.getActiveItemStack();if(stack.isEmpty())return;
  boolean managed=com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)||com.glowingfederal.moderncombatmovement.compat.Crossbows.known(stack)||com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)!=null||stack.getItem()==net.minecraft.init.Items.SHIELD&&com.glowingfederal.moderncombatmovement.config.Settings.shields(true);
  if(managed&&(!ClientControls.valid()||!mc.gameSettings.keyBindUseItem.isKeyDown())){if(mc.playerController!=null)mc.playerController.onStoppedUsingItem(mc.player);else entity.resetActiveHand();}
 }
}
