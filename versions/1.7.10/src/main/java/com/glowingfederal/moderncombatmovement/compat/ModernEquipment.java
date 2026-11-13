package com.glowingfederal.moderncombatmovement.compat;

import java.io.File;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import com.glowingfederal.moderncombatmovement.Identity;
import com.glowingfederal.moderncombatmovement.api.*;
import com.glowingfederal.moderncombatmovement.combat.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
/** Explicit content ownership; UAB presence suppresses duplicate equipment and acquisition materials. */
public final class ModernEquipment {
 private ModernEquipment(){}
 public static SpearSpec spear(ItemStack stack){
  if(stack==null)return null;String id=CombatHooks.id(stack);
  for(SpearSpec spec:SpearSpec.values())if(id.equals(Identity.MOD_ID+":"+spec.material+"_spear")||id.equals("etfuturum:"+spec.material+"_spear"))return spec;
  return null;
 }
 public static boolean mace(ItemStack stack){if(stack==null)return false;String id=CombatHooks.id(stack);return id.equals(Identity.MOD_ID+":mace")||id.equals("etfuturum:mace");}
 public static boolean enabled(EntityPlayer p){return Settings.kit(p.worldObj.isRemote)&&Settings.combat(p.worldObj.isRemote);}
 public static boolean spearUsing(EntityPlayer p){return enabled(p)&&spear(((PlayerUseAccess)p).mcm$useStack())!=null;}
 public static void register(File file){
  EquipmentEnchantments.register(file);
  String namespace=Content.uab?"etfuturum":Identity.MOD_ID;
  for(SpearSpec spec:SpearSpec.values()){
   String name=spec.material+"_spear";
   CombatApi.register(namespace+":"+name,new ItemProperties(ItemProperties.Kind.SPEAR,spec.attackSpeed(),spec.jabDamage(),false,false));
   if(!Content.uab){Item item=new CombatWeaponItem(spec);add(name,item);Object material=material(spec);if(material!=null)recipe(name,item,material);}
  }
  CombatApi.register(namespace+":mace",new ItemProperties(ItemProperties.Kind.MACE,.6,6,false,false));
  if(!Content.uab)add("mace",new CombatWeaponItem(null));
  Crystals.register();
  CombatConsumables.init();
 }
 private static Object material(SpearSpec spec){
  switch(spec){case WOODEN:return Blocks.planks;case STONE:return Blocks.cobblestone;case IRON:return Items.iron_ingot;case GOLDEN:return Items.gold_ingot;case DIAMOND:return Items.diamond;default:return null;}
 }
 private static void recipe(String name,Item item,Object material){cpw.mods.fml.common.registry.GameRegistry.addRecipe(new ItemStack(item),"  X"," # ","#  ",'X',material,'#',Items.stick);}
 private static void add(String name,Item item){item.setUnlocalizedName("moderncombatmovement."+name);item.setTextureName(Identity.MOD_ID+":"+name); cpw.mods.fml.common.registry.GameRegistry.registerItem(item,name); Content.ITEMS.put(name,item);}
}
