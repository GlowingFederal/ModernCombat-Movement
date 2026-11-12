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
 public static final IAttribute ATTACK_SPEED=new RangedAttribute(null,"generic.attackSpeed",4,0,1024).setShouldWatch(true);
 private static final UUID SPEED_ID=UUID.fromString("09296519-02d7-4d0a-b226-8b9035f29cae");
 private CombatHooks() {}
 public static ItemStack held(EntityPlayer p) { return p.getCurrentEquippedItem(); }
 public static String id(ItemStack stack) { if(stack==null) return "minecraft:air"; Object id=Item.itemRegistry.getNameForObject(stack.getItem()); return id==null ? "" : id.toString(); }
 public static ItemProperties profile(EntityPlayer p) { return CombatApi.find(id(held(p))); }
 public static boolean managed(DamageSource source) {
  // Indirect/custom projectiles are excluded even if their shooter holds a supported sword.
  return source!=null && source.getSourceOfDamage()==source.getEntity() && source.getEntity() instanceof EntityPlayer && profile((EntityPlayer)source.getEntity())!=null;
 }
 public static void registerAttribute(EntityPlayer p) { if(p.getEntityAttribute(ATTACK_SPEED)==null) p.getAttributeMap().registerAttribute(ATTACK_SPEED); }
 public static void update(EntityPlayer p) {
  ItemProperties profile=profile(p); IAttributeInstance attribute=p.getEntityAttribute(ATTACK_SPEED);
  if(attribute==null) { registerAttribute(p); attribute=p.getEntityAttribute(ATTACK_SPEED); }
  AttributeModifier previous=attribute.getModifier(SPEED_ID); if(previous!=null) attribute.removeModifier(previous);
  if(profile!=null && com.glowingfederal.moderncombatmovement.config.Settings.combat(p.worldObj.isRemote)) {
   double intrinsic=4;
   
   double correction=profile.attackSpeed-intrinsic;
   if(correction!=0) attribute.applyModifier(new AttributeModifier(SPEED_ID,"MCM intrinsic item attack speed",correction,0).setSaved(false));
  }
  PlayerState s=PlayerHooks.state(p); String item=id(held(p));
  if(!item.equals(s.lastWeapon)) { s.lastWeapon=item; s.attackTicks=0; }
 }
 public static double speed(EntityPlayer p) { IAttributeInstance a=p.getEntityAttribute(ATTACK_SPEED); return a==null ? 4 : a.getAttributeValue(); }
}
