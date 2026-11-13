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
 public static boolean known(ItemStack stack){return stack==null||ARMOR.contains(CombatHooks.id(stack));}
 public static ItemStack[] pieces(EntityLivingBase e){ItemStack[] result=new ItemStack[4];for(int i=0;i<4;i++)result[i]=e instanceof EntityPlayer?((EntityPlayer)e).inventory.armorInventory[i]:e.getEquipmentInSlot(i+1);return result;}
 public static boolean supported(ItemStack[] pieces){for(ItemStack piece:pieces)if(!known(piece))return false;return true;}
 public static boolean managed(DamageSource s){
  if(s==null)return false;if(CombatHooks.managed(s)||s instanceof com.glowingfederal.moderncombatmovement.movement.FlightWallDamage||s instanceof CombatBlastDamageSource)return true;
  Entity direct=s.getSourceOfDamage();
  return direct!=null&&(direct.getClass()==net.minecraft.entity.projectile.EntityArrow.class);
 }
 public static float reduce(EntityLivingBase e,DamageSource source,float damage){
  if(!Settings.combat(e.worldObj.isRemote)||!managed(source)||source.isUnblockable())return Float.NaN;
  ItemStack[] armor=pieces(e);if(!supported(armor))return Float.NaN;
  double points=0,toughness=0;
  for(ItemStack stack:armor){if(stack==null||!(stack.getItem() instanceof ItemArmor))continue;points+=((ItemArmor)stack.getItem()).damageReduceAmount;String id=CombatHooks.id(stack);if(id.startsWith("minecraft:diamond_"))toughness+=2;else if(id.startsWith("moderncombatmovement:netherite_")||id.startsWith("etfuturum:netherite_"))toughness+=3;}
  return WeaponMath.armor(damage,points,toughness,breach(source));
 }
 public static int breach(DamageSource source){Entity e=source.getEntity();return e instanceof EntityPlayer&&source.getSourceOfDamage()==e&&MaceMechanics.owns((EntityPlayer)e)?EquipmentEnchantments.level(EquipmentEnchantments.breach,CombatHooks.held((EntityPlayer)e)):0;}
 public static int protection(ItemStack[] inventory,DamageSource source){
  int points=0;for(ItemStack stack:inventory){if(stack==null)continue;NBTTagList list=stack.getEnchantmentTagList();if(list==null)continue;
   for(int i=0;i<list.tagCount();i++){NBTTagCompound tag=list.getCompoundTagAt(i);int id=tag.getShort("id"),level=tag.getShort("lvl");
    if(id==0)points+=level;else if(id==1&&source.isFireDamage())points+=2*level;else if(id==2&&source==DamageSource.fall)points+=3*level;else if(id==3&&source.isExplosion())points+=2*level;else if(id==4&&source.isProjectile())points+=2*level;
    else if(id>=5&&id<=255&&Enchantment.getEnchantmentById(id)!=null)points+=Enchantment.getEnchantmentById(id).calcModifierDamage(level,source);
   }
  }
  return Math.min(20,Math.max(0,points));
 }
 public static void update(EntityPlayer p){
  IAttributeInstance blast=p.getEntityAttribute(EXPLOSION_RESISTANCE);if(blast==null)blast=p.getAttributeMap().registerAttribute(EXPLOSION_RESISTANCE);
  double explosion=0,netherite=0,provided=0;
  ItemStack[] armor=pieces(p);for(int slot=0;slot<armor.length;slot++){ItemStack stack=armor[slot];if(stack==null)continue;explosion+=.15*EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId,stack);
   String id=CombatHooks.id(stack);boolean piece=id.startsWith("moderncombatmovement:netherite_")||id.startsWith("etfuturum:netherite_");
   if(piece){netherite+=.1;for(AttributeModifier m:stack.getAttributeModifiers().get("generic.knockbackResistance"))if(m.getOperation()==0)provided+=m.getAmount();}
  }
  boolean enabled=Settings.knockback(p.worldObj.isRemote);
  modifier(blast,BLAST,"MCM blast protection",enabled?explosion:0);
  modifier(p.getEntityAttribute(SharedMonsterAttributes.knockbackResistance),NETHERITE,"MCM netherite armor",enabled?Math.max(0,netherite-provided):0);
 }
 private static void modifier(IAttributeInstance attr,UUID id,String name,double value){if(attr==null)return;AttributeModifier old=attr.getModifier(id);if(old!=null&&Double.compare(old.getAmount(),value)==0)return;if(old!=null)attr.removeModifier(old);if(value!=0)attr.applyModifier(new AttributeModifier(id,name,value,0).setSaved(false));}
 public static double explosionResistance(Entity e){
  if(!(e instanceof EntityLivingBase))return 0;EntityLivingBase living=(EntityLivingBase)e;IAttributeInstance attr=living.getEntityAttribute(EXPLOSION_RESISTANCE);
  if(attr!=null)return attr.getAttributeValue();double sum=0;for(ItemStack stack:pieces(living))if(!(stack==null))sum+=.15*EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId,stack);return Math.min(1,sum);
 }
}
