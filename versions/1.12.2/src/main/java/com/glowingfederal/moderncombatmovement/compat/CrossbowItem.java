package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
public final class CrossbowItem extends Item {
 public CrossbowItem() { setMaxStackSize(1);setMaxDamage(465);setCreativeTab(net.minecraft.creativetab.CreativeTabs.COMBAT);setTranslationKey("moderncombatmovement.crossbow");addPropertyOverride(new net.minecraft.util.ResourceLocation("charged"),(s,w,e)->Crossbows.charged(s)?1:0);addPropertyOverride(new net.minecraft.util.ResourceLocation("pulling"),(s,w,e)->e!=null&&e.isHandActive()&&e.getActiveItemStack()==s&&!Crossbows.charged(s)?1:0);addPropertyOverride(new net.minecraft.util.ResourceLocation("pull"),(s,w,e)->e!=null&&e.getActiveItemStack()==s?(float)e.getItemInUseMaxCount()/Crossbows.chargeTime(s):0); }
 @Override public int getMaxItemUseDuration(ItemStack stack) { return Crossbows.duration(stack); }
 @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.NONE; }
 @Override public int getItemEnchantability() { return 1; }
 @Override public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World w,EntityPlayer p,net.minecraft.util.EnumHand hand) { ItemStack stack=p.getHeldItem(hand);return new net.minecraft.util.ActionResult<ItemStack>(Crossbows.use(stack,p,hand)?net.minecraft.util.EnumActionResult.SUCCESS:net.minecraft.util.EnumActionResult.FAIL,stack); }
 @Override public void onPlayerStoppedUsing(ItemStack stack,World world,EntityLivingBase user,int remaining) { if(user instanceof EntityPlayer) Crossbows.release(stack,(EntityPlayer)user,remaining); }
 @Override public void onUsingTick(ItemStack stack,EntityLivingBase user,int remaining) { if(user instanceof EntityPlayer) Crossbows.tick(stack,(EntityPlayer)user,remaining); }

}
