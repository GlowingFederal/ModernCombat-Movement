package com.glowingfederal.moderncombatmovement.compat;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import com.glowingfederal.moderncombatmovement.Identity;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class Content {
 public static boolean uab;
 public static final Map<String,Item> ITEMS=new LinkedHashMap<String,Item>();
 private Content() {}
 public static void register() {
  uab=Loader.isModLoaded("etfuturum");
  if(uab) { registerProfiles("etfuturum"); return; }
  if(!Settings.fallbackEquipment) return;
  registerProfiles(Identity.MOD_ID);
  Item.ToolMaterial tool=EnumHelper.addToolMaterial("MCM_NETHERITE",4,2031,9F,4F,15);
  add("netherite_sword",new ItemSword(tool));
  add("netherite_pickaxe",new ItemPickaxe(tool) {});
  add("netherite_shovel",new ItemSpade(tool));
  add("netherite_axe",new ItemAxe(tool) {});
  add("netherite_hoe",new ItemHoe(tool));
  ItemArmor.ArmorMaterial armor=EnumHelper.addArmorMaterial("MCM_NETHERITE","moderncombatmovement:netherite",37,new int[]{3,8,6,3},15);
  add("netherite_helmet",new Armor(armor,0)); add("netherite_chestplate",new Armor(armor,1));
  add("netherite_leggings",new Armor(armor,2)); add("netherite_boots",new Armor(armor,3));
  Item.ToolMaterial copper=EnumHelper.addToolMaterial("MCM_COPPER",1,190,5F,1F,13);
  add("copper_sword",new ItemSword(copper));add("copper_pickaxe",new ItemPickaxe(copper) {});
  add("copper_shovel",new ItemSpade(copper));add("copper_axe",new ItemAxe(copper) {});add("copper_hoe",new ItemHoe(copper));
  ItemArmor.ArmorMaterial c=EnumHelper.addArmorMaterial("MCM_COPPER","moderncombatmovement:copper",11,new int[]{2,4,3,1},8);
  add("copper_helmet",new CopperArmor(c,0));add("copper_chestplate",new CopperArmor(c,1));add("copper_leggings",new CopperArmor(c,2));add("copper_boots",new CopperArmor(c,3));

 }
 private static void registerProfiles(String namespace) {
  String[] tools={"sword","pickaxe","shovel","axe","hoe"};
  double[] speed={1.6,1.2,1,1,4},damage={8,6,6.5,10,1};
  for(int i=0;i<tools.length;i++) com.glowingfederal.moderncombatmovement.api.CombatApi.register(namespace+":netherite_"+tools[i],new com.glowingfederal.moderncombatmovement.api.ItemProperties(i==0 ? com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.SWORD : i==3 ? com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.AXE : com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.TOOL,speed[i],damage[i],i==0,false));
  double[] copperSpeed={1.6,1.2,1,.8,2},copperDamage={5,3,3.5,9,1};
  for(int i=0;i<tools.length;i++) com.glowingfederal.moderncombatmovement.api.CombatApi.register(namespace+":copper_"+tools[i],new com.glowingfederal.moderncombatmovement.api.ItemProperties(i==0?com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.SWORD:i==3?com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.AXE:com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.TOOL,copperSpeed[i],copperDamage[i],i==0,false));

 }
 private static void add(String name,Item item) {
  item.setUnlocalizedName("moderncombatmovement."+name);
  GameRegistry.registerItem(item,name);
  ITEMS.put(name,item);
 }
 private static final class Armor extends ItemArmor {
  Armor(ArmorMaterial m,int slot) { super(m,0,slot); }
  @Override public String getArmorTexture(ItemStack stack,net.minecraft.entity.Entity entity,int slot,String type) {
   return "moderncombatmovement:textures/models/armor/netherite_layer_"+(slot==2 ? 2 : 1)+".png";
  }
 }
 private static final class CopperArmor extends ItemArmor {
  CopperArmor(ArmorMaterial m,int slot){super(m,0,slot);}
  @Override public String getArmorTexture(ItemStack stack,net.minecraft.entity.Entity entity,int slot,String type){return "moderncombatmovement:textures/models/armor/copper_layer_"+(slot==2?2:1)+".png";}
 }

}
