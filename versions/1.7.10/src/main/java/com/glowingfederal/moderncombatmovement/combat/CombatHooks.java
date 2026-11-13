package com.glowingfederal.moderncombatmovement.combat;

import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.util.DamageSource;
import com.glowingfederal.moderncombatmovement.api.*;
import com.glowingfederal.moderncombatmovement.pose.*;
public final class CombatHooks {
 public static final IAttribute ATTACK_SPEED=new RangedAttribute("generic.attackSpeed",4,0,1024).setShouldWatch(true);
 private static final UUID SPEED_ID=UUID.fromString("09296519-02d7-4d0a-b226-8b9035f29cae");
 private CombatHooks() {}
 public static ItemStack held(EntityPlayer p) { return p.getCurrentEquippedItem(); }
 public static String id(ItemStack stack) { if(stack==null) return "minecraft:air"; Object id=Item.itemRegistry.getNameForObject(stack.getItem()); return id==null ? "" : id.toString(); }
 public static ItemProperties profile(EntityPlayer p) { return CombatApi.find(id(held(p))); }
 public static boolean managed(DamageSource source) {
  // Indirect/custom projectiles are excluded even if their shooter holds a supported sword.
  return source!=null && source instanceof com.glowingfederal.moderncombatmovement.compat.BoostRocketDamage||source!=null && source.getSourceOfDamage() instanceof com.glowingfederal.moderncombatmovement.compat.CrossbowBolt || source!=null && source.getSourceOfDamage()==source.getEntity() && source.getEntity() instanceof EntityPlayer && profile((EntityPlayer)source.getEntity())!=null;
 }
 public static void registerAttribute(EntityPlayer p) { if(p.getEntityAttribute(ATTACK_SPEED)==null) p.getAttributeMap().registerAttribute(ATTACK_SPEED); }
 public static void update(EntityPlayer p) {
  ArmorRules.update(p);
  ItemProperties profile=profile(p); IAttributeInstance attribute=p.getEntityAttribute(ATTACK_SPEED);
  if(attribute==null) { registerAttribute(p); attribute=p.getEntityAttribute(ATTACK_SPEED); }
  AttributeModifier previous=attribute.getModifier(SPEED_ID);
  if(profile!=null && com.glowingfederal.moderncombatmovement.config.Settings.combat(p.worldObj.isRemote)) {
   double intrinsic=4;

   double correction=profile.attackSpeed-intrinsic;
   if(previous==null || Double.compare(previous.getAmount(),correction)!=0) {
    if(previous!=null) attribute.removeModifier(previous);
    if(correction!=0) attribute.applyModifier(new AttributeModifier(SPEED_ID,"MCM intrinsic item attack speed",correction,0).setSaved(false));
   }
  }
  else if(previous!=null) attribute.removeModifier(previous);
  PlayerState s=PlayerHooks.state(p); String item=id(held(p));
  if(!item.equals(s.lastWeapon)) { s.lastWeapon=item; s.attackTicks=0; }
 }
 public static double speed(EntityPlayer p) { IAttributeInstance a=p.getEntityAttribute(ATTACK_SPEED); return a==null ? 4 : a.getAttributeValue(); }
 /** Normalize only the opted-in item's intrinsic modifier; retain commands and unrelated attribute modifiers. */
 public static float attackDamage(EntityPlayer p){
  IAttributeInstance attr=p.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.attackDamage);ItemProperties profile=profile(p);ItemStack stack=held(p);
  if(profile==null)return(float)attr.getAttributeValue();
  java.util.Set<UUID> intrinsic=new java.util.HashSet<UUID>();
  boolean custom=stack!=null&&stack.hasTagCompound()&&stack.getTagCompound().hasKey("AttributeModifiers",9);
  if(!custom&&stack!=null)for(AttributeModifier m:stack.getAttributeModifiers().get("generic.attackDamage"))if(m.getOperation()==0)intrinsic.add(m.getID());
  UUID strength=UUID.fromString("648d7064-6a60-4f59-8abe-c2c23a6dd7a9"),weakness=UUID.fromString("22653b89-116e-49dc-9b6b-9971489b5be5");
  double start=attr.getBaseValue();
  for(AttributeModifier m:modifiers(attr,0))if(!intrinsic.contains(m.getID())&&!m.getID().equals(strength)&&!m.getID().equals(weakness))start+=m.getAmount();
  if(!custom)start+=profile.attackDamage-1;
  if(p.isPotionActive(net.minecraft.potion.Potion.damageBoost))start+=3*(p.getActivePotionEffect(net.minecraft.potion.Potion.damageBoost).getAmplifier()+1);if(p.isPotionActive(net.minecraft.potion.Potion.weakness))start-=4*(p.getActivePotionEffect(net.minecraft.potion.Potion.weakness).getAmplifier()+1);
  double value=start;for(AttributeModifier m:modifiers(attr,1))value+=start*m.getAmount();
  for(AttributeModifier m:modifiers(attr,2))if(!m.getID().equals(strength)&&!m.getID().equals(weakness))value*=1+m.getAmount();
  return(float)Math.max(0,Math.min(2048,value));
 }

 private static java.util.List<AttributeModifier> modifiers(IAttributeInstance attr,int operation){java.util.List<AttributeModifier> result=new java.util.ArrayList<AttributeModifier>();for(Object object:attr.func_111122_c()){AttributeModifier modifier=(AttributeModifier)object;if(modifier.getOperation()==operation)result.add(modifier);}return result;}

}
