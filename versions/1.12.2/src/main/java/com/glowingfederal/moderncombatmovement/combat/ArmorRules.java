package com.glowingfederal.moderncombatmovement.combat;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.item.*;
import net.minecraft.enchantment.*;
import net.minecraft.nbt.*;
import net.minecraft.util.DamageSource;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
/** Native armor and explicit MCM/UAB equipment only; unknown special armor retains Forge's owner. */
public final class ArmorRules {
 public static final IAttribute EXPLOSION_RESISTANCE=new RangedAttribute(null,"generic.explosionKnockbackResistance",0,0,1).setShouldWatch(true);
 private static final UUID BLAST=UUID.fromString("22997a93-e17c-4ee8-9f15-1c205f1075af"),NETHERITE=UUID.fromString("2e111360-5858-4b83-9735-24d932a2a451");
 private static final Set<String> ARMOR=new HashSet<String>();
 static{for(String m:new String[]{"leather","chainmail","iron","golden","diamond"})for(String p:new String[]{"helmet","chestplate","leggings","boots"})ARMOR.add("minecraft:"+m+"_"+p);for(String n:new String[]{"moderncombatmovement","etfuturum"})for(String m:new String[]{"netherite","copper"})for(String p:new String[]{"helmet","chestplate","leggings","boots"})ARMOR.add(n+":"+m+"_"+p);ARMOR.add("etfuturum:turtle_helmet");ARMOR.add("minecraft:turtle_helmet");ARMOR.add("minecraft:elytra");ARMOR.add("moderncombatmovement:elytra");ARMOR.add("etfuturum:elytra");}
 private ArmorRules(){}
 public static boolean known(ItemStack stack){return stack.isEmpty()||ARMOR.contains(CombatHooks.id(stack));}
 public static ItemStack[] pieces(EntityLivingBase e){net.minecraft.inventory.EntityEquipmentSlot[] slots={net.minecraft.inventory.EntityEquipmentSlot.FEET,net.minecraft.inventory.EntityEquipmentSlot.LEGS,net.minecraft.inventory.EntityEquipmentSlot.CHEST,net.minecraft.inventory.EntityEquipmentSlot.HEAD};ItemStack[] result=new ItemStack[4];for(int i=0;i<4;i++)result[i]=e.getItemStackFromSlot(slots[i]);return result;}
 public static boolean supported(ItemStack[] pieces){for(ItemStack piece:pieces)if(!known(piece))return false;return true;}
 public static boolean managed(DamageSource s){
  if(s==null)return false;if(CombatHooks.managed(s)||s instanceof com.glowingfederal.moderncombatmovement.movement.FlightWallDamage||s instanceof CombatBlastDamageSource||s instanceof SpearDamageSource)return true;
  Entity direct=s.getImmediateSource();
  return direct!=null&&(direct.getClass()==net.minecraft.entity.projectile.EntityArrow.class||direct.getClass()==net.minecraft.entity.projectile.EntityTippedArrow.class||direct.getClass()==net.minecraft.entity.projectile.EntitySpectralArrow.class);
 }
 public static float reduce(EntityLivingBase e,DamageSource source,float damage){
  if(!Settings.combat(e.world.isRemote)||!managed(source)||source.isUnblockable())return Float.NaN;
  ItemStack[] armor=pieces(e);if(!supported(armor))return Float.NaN;
  double points=0,toughness=0;
  points=e.getTotalArmorValue();toughness=e.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
  return WeaponMath.armor(damage,points,toughness,breach(source));
 }
 public static int breach(DamageSource source){Entity e=source.getTrueSource();return e instanceof EntityPlayer&&source.getImmediateSource()==e&&MaceMechanics.owns((EntityPlayer)e)?EquipmentEnchantments.level(EquipmentEnchantments.breach,CombatHooks.held((EntityPlayer)e)):0;}
 public static void update(EntityPlayer p){
  IAttributeInstance blast=p.getEntityAttribute(EXPLOSION_RESISTANCE);if(blast==null)blast=p.getAttributeMap().registerAttribute(EXPLOSION_RESISTANCE);
  double explosion=0,netherite=0,provided=0;
  ItemStack[] armor=pieces(p);for(int slot=0;slot<armor.length;slot++){ItemStack stack=armor[slot];if(stack.isEmpty())continue;explosion+=.15*EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.BLAST_PROTECTION,stack);
   String id=CombatHooks.id(stack);boolean piece=id.startsWith("moderncombatmovement:netherite_")||id.startsWith("etfuturum:netherite_");
   if(piece){netherite+=.1;for(AttributeModifier m:stack.getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot.values()[slot+2]).get("generic.knockbackResistance"))if(m.getOperation()==0)provided+=m.getAmount();}
  }
  boolean enabled=Settings.knockback(p.world.isRemote);
  modifier(blast,BLAST,"MCM blast protection",enabled?explosion:0);
  modifier(p.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE),NETHERITE,"MCM netherite armor",enabled?Math.max(0,netherite-provided):0);
 }
 private static void modifier(IAttributeInstance attr,UUID id,String name,double value){if(attr==null)return;AttributeModifier old=attr.getModifier(id);if(old!=null&&Double.compare(old.getAmount(),value)==0)return;if(old!=null)attr.removeModifier(old);if(value!=0)attr.applyModifier(new AttributeModifier(id,name,value,0).setSaved(false));}
 public static double explosionResistance(Entity e){
  if(!(e instanceof EntityLivingBase))return 0;EntityLivingBase living=(EntityLivingBase)e;IAttributeInstance attr=living.getEntityAttribute(EXPLOSION_RESISTANCE);
  if(attr!=null)return attr.getAttributeValue();double sum=0;for(ItemStack stack:pieces(living))if(!(stack.isEmpty()))sum+=.15*EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.BLAST_PROTECTION,stack);return Math.min(1,sum);
 }
}
