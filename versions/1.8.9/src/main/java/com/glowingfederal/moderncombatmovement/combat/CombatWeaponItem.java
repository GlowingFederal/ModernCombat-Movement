package com.glowingfederal.moderncombatmovement.combat;

import java.util.UUID;
import com.google.common.collect.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;
/** Standalone item only. Gameplay dispatch is owned by the weapon subsystem, not ItemSword inheritance. */
public final class CombatWeaponItem extends Item {
 private final SpearSpec spear;
 private final float damage;
 public CombatWeaponItem(SpearSpec spear) {
  this.spear=spear;damage=spear==null?6:spear.jabDamage();
  setMaxStackSize(1);setMaxDamage(spear==null?500:spear.durability);
  setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
 }
 @Override public int getItemEnchantability(){return spear==null?15:spear.enchantability;}
 @Override public int getMaxItemUseDuration(ItemStack stack){return spear==null?0:72000;}
 @Override public EnumAction getItemUseAction(ItemStack stack){return EnumAction.NONE;}
 @Override public Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> getItemAttributeModifiers() {
  Multimap<String,net.minecraft.entity.ai.attributes.AttributeModifier> map=HashMultimap.create(super.getItemAttributeModifiers());

  map.put("generic.attackDamage",new net.minecraft.entity.ai.attributes.AttributeModifier(UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf"),"Weapon modifier",damage-1,0));

  return map;
 }
 @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer p){if(spear!=null)SpearMechanics.use(stack,p);return stack;}
 @Override public void onUsingTick(ItemStack stack,EntityPlayer user,int remaining){if(user instanceof EntityPlayer)SpearMechanics.charge(stack,(EntityPlayer)user,remaining);}
 @Override public boolean hitEntity(ItemStack stack,EntityLivingBase target,EntityLivingBase attacker){if(spear==null && attacker instanceof EntityPlayer)MaceMechanics.impact((EntityPlayer)attacker,target);stack.damageItem(1,attacker);return true;}
}
