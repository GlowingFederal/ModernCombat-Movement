package com.glowingfederal.moderncombatmovement.combat;

import java.io.File;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import net.minecraftforge.common.config.Configuration;
import com.glowingfederal.moderncombatmovement.compat.*;
public final class EquipmentEnchantments {
 public static Enchantment density,breach,windBurst,lunge,mending,sweeping;
 private static Enchantment externalMending;
 private EquipmentEnchantments(){}
 public static void register(File file){if(Content.uab)try{externalMending=(Enchantment)Class.forName("ganymedes01.etfuturum.ModEnchantments").getField("mending").get(null);}catch(ReflectiveOperationException unavailable){}Configuration c=new Configuration(file);c.load();density=create(c,"density","density",240,5,5,8,25);breach=create(c,"breach","breach",241,4,15,9,65);windBurst=create(c,"wind_burst","windBurst",242,3,15,9,65);lunge=create(c,"lunge","lunge",243,3,5,8,25);mending=externalMending;if(mending==null&&!Content.uab)mending=repairEnchantment(c);externalMending=mending;sweeping=sweepEnchantment(c);RepairEnchantments.init();if(c.hasChanged())c.save();}
 public static int level(Enchantment e,ItemStack s){return Crossbows.level(e,s);}
 private static Enchantment create(Configuration c,String id,String field,int preferred,int max,int min,int step,int high){
  if(Content.uab)try{Object found=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(field).get(null);if(found instanceof Enchantment)return(Enchantment)found;}catch(ReflectiveOperationException absent){}
  int index=c.get("Compatibility",id+"EnchantmentId",preferred,"Reserved legacy enchantment ID; must match on both sides.").getInt();if(index<0||index>255||Enchantment.getEnchantmentById(index)!=null)throw new IllegalStateException("Occupied/out of range "+id+" enchantment ID "+index);Enchantment made=new ModernEnchantment(index,id,max,min,step,high);Enchantment.addToBookList(made);return made;
 }
 public static boolean elytraAllowed(Enchantment e){return e==Enchantment.unbreaking||e!=null&&e==externalMending;}
 public static boolean maceAllowed(Enchantment e){return e==externalMending&&e!=null||e==density||e==breach||e==windBurst||e==Enchantment.smite||e==Enchantment.baneOfArthropods||e==Enchantment.fireAspect||e==Enchantment.unbreaking ;}
 public static boolean spearAllowed(Enchantment e){return e==externalMending&&e!=null||e==lunge||e instanceof EnchantmentDamage||e==Enchantment.knockback||e==Enchantment.fireAspect||e==Enchantment.looting||e==Enchantment.unbreaking ;}
 private static final class ModernEnchantment extends Enchantment {
  final String key;final int max,min,step,high;
  ModernEnchantment(int id,String key,int max,int min,int step,int high){super(id,new net.minecraft.util.ResourceLocation("moderncombatmovement",key),2,EnumEnchantmentType.ALL);this.key=key;this.max=max;this.min=min;this.step=step;this.high=high;setName("moderncombatmovement."+key);}
  @Override public int getMaxLevel(){return max;}
  @Override public boolean canApplyAtEnchantingTable(ItemStack stack){return !key.equals("wind_burst")&&canApply(stack);}
  @Override public int getMinEnchantability(int level){return min+(level-1)*step;}
  @Override public int getMaxEnchantability(int level){return high+(level-1)*step;}
  @Override public boolean canApply(ItemStack s){return key.equals("lunge")?ModernEquipment.spear(s)!=null:ModernEquipment.mace(s);}
  @Override public boolean canApplyTogether(Enchantment e){return super.canApplyTogether(e)&&!((this==density||this==breach)&&(e==density||e==breach||e instanceof EnchantmentDamage));}
 }

 private static int vacant(Configuration c,String name,int preferred){int index=c.get("Compatibility",name+"EnchantmentId",preferred,"Reserved legacy enchantment ID; must be free and match on both sides.").getInt();if(index<0||index>255||Enchantment.getEnchantmentById(index)!=null)throw new IllegalStateException("Occupied/out of range "+name+" enchantment ID "+index);return index;}
 private static Enchantment repairEnchantment(Configuration c){Enchantment e=new Mending(vacant(c,"mending",203));Enchantment.addToBookList(e);return e;}
 private static Enchantment sweepEnchantment(Configuration c){Enchantment e=new Sweeping(vacant(c,"sweeping",244));Enchantment.addToBookList(e);return e;}
 /** Adapted from UAB's Mending enchantment; retain treasure acquisition and breakable eligibility. */
 private static final class Mending extends Enchantment {
  Mending(int index){super(index,new net.minecraft.util.ResourceLocation("moderncombatmovement","mending"),1,EnumEnchantmentType.BREAKABLE);setName("moderncombatmovement.mending");}
  @Override public int getMinEnchantability(int level){return 25;}
  @Override public int getMaxEnchantability(int level){return 75;}
  @Override public boolean canApplyAtEnchantingTable(ItemStack stack){return false;}
  @Override public boolean canApplyTogether(Enchantment other){return super.canApplyTogether(other)&&other!=Enchantment.infinity;}
 }
 private static final class Sweeping extends Enchantment {
  Sweeping(int index){super(index,new net.minecraft.util.ResourceLocation("moderncombatmovement","sweeping"),2,EnumEnchantmentType.WEAPON);setName("moderncombatmovement.sweeping");}
  @Override public int getMaxLevel(){return 3;}
  @Override public int getMinEnchantability(int level){return 5+(level-1)*9;}
  @Override public int getMaxEnchantability(int level){return getMinEnchantability(level)+15;}
  @Override public boolean canApply(ItemStack stack){com.glowingfederal.moderncombatmovement.api.ItemProperties profile=com.glowingfederal.moderncombatmovement.api.CombatApi.find(CombatHooks.id(stack));return profile!=null&&profile.sweeping;}
 }

}
