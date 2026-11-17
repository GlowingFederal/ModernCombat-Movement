/* Item component ownership adapted from UAB ItemPhysics; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.ItemStack;
/** Exact registered MCM equipment only; UAB retains its own fireproof-item settings. */
public final class EquipmentItems {
 private EquipmentItems(){}
 public static boolean fireproof(ItemStack stack){
  if(stack==null)return false;
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack);
  return id.startsWith("moderncombatmovement:netherite_")&&Content.ITEMS.get(id.substring("moderncombatmovement:".length()))==stack.getItem();
 }

 /** Native/UAB repair ingredients only. Missing materials defer material repair; combining and Mending remain native. */
 public static Boolean repair(ItemStack stack,ItemStack ingredient){
  if(stack==null||ingredient==null)return null;
  com.glowingfederal.moderncombatmovement.combat.SpearSpec spear=ModernEquipment.spear(stack);
  if(spear!=null)return material(spear.material,ingredient);
  if(ModernEquipment.mace(stack))return com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ingredient).equals("etfuturum:breeze_rod");
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack);
  if(!id.startsWith("moderncombatmovement:")||Content.ITEMS.get(id.substring("moderncombatmovement:".length()))!=stack.getItem())return null;
  if(id.startsWith("moderncombatmovement:copper_"))return material("copper",ingredient);
  if(id.startsWith("moderncombatmovement:netherite_"))return material("netherite",ingredient);
  return null;
 }
 private static boolean material(String material,ItemStack ingredient){
  String ore=material.equals("wooden")?"plankWood":material.equals("stone")?"cobblestone":material.equals("iron")?"ingotIron":material.equals("golden")?"ingotGold":material.equals("diamond")?"gemDiamond":material.equals("copper")?"ingotCopper":"ingotNetherite";
  for(int id:net.minecraftforge.oredict.OreDictionary.getOreIDs(ingredient))if(net.minecraftforge.oredict.OreDictionary.getOreName(id).equals(ore))return true;
  return false;
 }
 public static boolean membrane(ItemStack ingredient){return ingredient!=null&&com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ingredient).equals("etfuturum:phantom_membrane");}
}
