package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.*;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
public final class Totems {
 private Totems() {}
 public static void register() {
  if(!Content.uab) { Item item=new Item().setUnlocalizedName("moderncombatmovement.totem_of_undying").setTextureName("moderncombatmovement:totem_of_undying").setMaxStackSize(1).setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);cpw.mods.fml.common.registry.GameRegistry.registerItem(item,"totem_of_undying");Content.ITEMS.put("totem_of_undying",item); }
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Totems());
 }
 private static boolean known(ItemStack stack) { if(stack==null) return false;String id=CombatHooks.id(stack);return id.equals("moderncombatmovement:totem_of_undying") || id.equals("etfuturum:totem_of_undying"); }
 @SubscribeEvent(priority=EventPriority.HIGHEST) public void death(LivingDeathEvent e) {
  EntityLivingBase living=e.entityLiving;
  if(living.worldObj.isRemote || !com.glowingfederal.moderncombatmovement.config.Settings.kit(false) || e.source.canHarmInCreative()) return;
  ItemStack stack=living.getHeldItem();boolean off=false;
  if(!known(stack)&&living instanceof EntityPlayer){stack=com.glowingfederal.moderncombatmovement.offhand.Offhands.get((EntityPlayer)living);off=true;}if(!known(stack))return;
  if(--stack.stackSize<=0) { if(off)com.glowingfederal.moderncombatmovement.offhand.Offhands.set((EntityPlayer)living,null);else if(living instanceof EntityPlayer) ((EntityPlayer)living).inventory.setInventorySlotContents(((EntityPlayer)living).inventory.currentItem,null);else living.setCurrentItemOrArmor(0,null); }
  e.setCanceled(true);living.clearActivePotions();living.setHealth(1);
  living.addPotionEffect(new PotionEffect(Potion.regeneration.id,900,1));living.addPotionEffect(new PotionEffect(Potion.potionTypes[22].id,100,1));living.addPotionEffect(new PotionEffect(Potion.fireResistance.id,800,0));
  living.worldObj.playSoundAtEntity(living,"random.levelup",1,1);living.worldObj.setEntityState(living,(byte)35);
 }
}
