/* Adapted from UAB ItemTrident/TridentItem; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;


import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

final class TridentItem extends Item {
    public TridentItem() { setMaxStackSize(1); setMaxDamage(250);setTranslationKey("moderncombatmovement.trident");setCreativeTab(net.minecraft.creativetab.CreativeTabs.COMBAT); }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.BOW; }
    @Override public int getMaxItemUseDuration(ItemStack stack) { return 72000; }
    @Override public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand){ItemStack stack=player.getHeldItem(hand);return new ActionResult<ItemStack>(Tridents.use(stack,player,hand)?EnumActionResult.SUCCESS:EnumActionResult.FAIL,stack);}
    @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityLivingBase user,int remaining){if(user instanceof EntityPlayer)Tridents.release(stack,(EntityPlayer)user,remaining);}
    @Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) { stack.damageItem(1, attacker); return true; }
    @Override public int getItemEnchantability() { return 1; }
    @Override public com.google.common.collect.Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier> getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot slot, ItemStack stack) {
        com.google.common.collect.Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier> attributes = super.getAttributeModifiers(slot, stack);
        if (slot == net.minecraft.inventory.EntityEquipmentSlot.MAINHAND) attributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new net.minecraft.entity.ai.attributes.AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 8, 0));
        return attributes;
    }
}
