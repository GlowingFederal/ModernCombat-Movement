package com.glowingfederal.moderncombatmovement.combat;

import java.io.File;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraftforge.common.config.Configuration;
import com.glowingfederal.moderncombatmovement.compat.*;
public final class EquipmentEnchantments {
 public static Enchantment density,breach,windBurst,lunge,mending,sweeping;
 private EquipmentEnchantments(){}
 public static void register(File file){Configuration c=new Configuration(file);c.load();density=create(c,"density","density",240,5,5,8,25);breach=create(c,"breach","breach",241,4,15,9,65);windBurst=create(c,"wind_burst","windBurst",242,3,15,9,65);lunge=create(c,"lunge","lunge",243,3,5,8,25);mending=net.minecraft.init.Enchantments.MENDING;sweeping=net.minecraft.init.Enchantments.SWEEPING;RepairEnchantments.init();if(c.hasChanged())c.save();}
 public static int level(Enchantment e,ItemStack s){return Crossbows.level(e,s);}
 private static Enchantment create(Configuration c,String id,String field,int preferred,int max,int min,int step,int high){
  if(Content.uab)try{Object found=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(field).get(null);if(found instanceof Enchantment)return(Enchantment)found;}catch(ReflectiveOperationException absent){}
  Enchantment found=Enchantment.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("etfuturum",id));if(found!=null)return found;Enchantment made=new ModernEnchantment(id,max,min,step,high).setRegistryName("moderncombatmovement",id);net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Enchantment.class).register(made);return made;
 }
 public static boolean elytraAllowed(Enchantment e){return e==net.minecraft.init.Enchantments.UNBREAKING||e==net.minecraft.init.Enchantments.MENDING||e==net.minecraft.init.Enchantments.BINDING_CURSE||e==net.minecraft.init.Enchantments.VANISHING_CURSE;}
 public static boolean maceAllowed(Enchantment e){return e==density||e==breach||e==windBurst||e==net.minecraft.init.Enchantments.SMITE||e==net.minecraft.init.Enchantments.BANE_OF_ARTHROPODS||e==net.minecraft.init.Enchantments.FIRE_ASPECT||e==net.minecraft.init.Enchantments.UNBREAKING ||e==net.minecraft.init.Enchantments.MENDING||e==net.minecraft.init.Enchantments.VANISHING_CURSE;}
 public static boolean spearAllowed(Enchantment e){return e==lunge||e instanceof EnchantmentDamage||e==net.minecraft.init.Enchantments.KNOCKBACK||e==net.minecraft.init.Enchantments.FIRE_ASPECT||e==net.minecraft.init.Enchantments.LOOTING||e==net.minecraft.init.Enchantments.UNBREAKING ||e==net.minecraft.init.Enchantments.MENDING||e==net.minecraft.init.Enchantments.VANISHING_CURSE;}
 private static final class ModernEnchantment extends Enchantment {
  final String key;final int max,min,step,high;
  ModernEnchantment(String key,int max,int min,int step,int high){super(key.equals("density")||key.equals("lunge")?Rarity.UNCOMMON:Rarity.RARE,EnumEnchantmentType.ALL,new net.minecraft.inventory.EntityEquipmentSlot[]{net.minecraft.inventory.EntityEquipmentSlot.MAINHAND});this.key=key;this.max=max;this.min=min;this.step=step;this.high=high;setName("moderncombatmovement."+key);}
  @Override public int getMaxLevel(){return max;}
  @Override public boolean isTreasureEnchantment(){return key.equals("wind_burst");}
  @Override public boolean canApplyAtEnchantingTable(ItemStack stack){return !key.equals("wind_burst")&&canApply(stack);}
  @Override public int getMinEnchantability(int level){return min+(level-1)*step;}
  @Override public int getMaxEnchantability(int level){return high+(level-1)*step;}
  @Override public boolean canApply(ItemStack s){return key.equals("lunge")?ModernEquipment.spear(s)!=null:ModernEquipment.mace(s);}
  @Override public boolean canApplyTogether(Enchantment e){return super.canApplyTogether(e)&&!((this==density||this==breach)&&(e==density||e==breach||e instanceof EnchantmentDamage));}
 }
}
