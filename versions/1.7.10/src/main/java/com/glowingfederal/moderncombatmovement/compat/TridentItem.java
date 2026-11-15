/* Adapted from UAB ItemTrident/TridentItem; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;



import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;

public final class TridentItem extends Item {
	public TridentItem() { setMaxStackSize(1); setMaxDamage(250); setUnlocalizedName("moderncombatmovement.trident"); setTextureName("moderncombatmovement:trident"); setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat); }
	@Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.bow; }
	@Override public int getMaxItemUseDuration(ItemStack stack) { return 72000; }
    @Override public ItemStack onItemRightClick(ItemStack stack,World world,EntityPlayer player){Tridents.use(stack,player);return stack;}
    @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityPlayer player,int remaining){Tridents.release(stack,player,remaining);}
	@Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1, attacker); return true; }
	@Override public int getItemEnchantability() { return 1; }
	@Override public com.google.common.collect.Multimap getItemAttributeModifiers() {
		com.google.common.collect.Multimap attributes = super.getItemAttributeModifiers();
		attributes.put(net.minecraft.entity.SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new net.minecraft.entity.ai.attributes.AttributeModifier(field_111210_e, "Weapon modifier", 8, 0)); return attributes;
	}
}
