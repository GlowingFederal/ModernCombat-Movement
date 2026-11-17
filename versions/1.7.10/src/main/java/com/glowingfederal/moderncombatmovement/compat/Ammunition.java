/* Adapted from UAB ItemArrowTipped and ServerEventHandler arrow callbacks; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.enchantment.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.registry.GameRegistry;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.offhand.Offhands;

/** Exact native-bow and registered ammunition boundary; foreign bows keep their own callbacks. */
public final class Ammunition {
 public static Item tipped,spectral;
 public static Potion glowing;
 private Ammunition(){}
 public static List<PotionEffect> effects(ItemStack stack){
  List<PotionEffect> result=new ArrayList<PotionEffect>();if(stack==null)return result;
  if(stack.hasTagCompound()&&stack.getTagCompound().hasKey("Potion",10)){
   // UAB's public single-effect format already stores its delivered duration.
   PotionEffect effect=PotionEffect.readCustomPotionEffectFromNBT(stack.getTagCompound().getCompoundTag("Potion"));if(effect!=null)result.add(effect);return result;
  }
  List<PotionEffect> raw=PotionDelivery.effects(stack);
  for(PotionEffect effect:raw){PotionEffect copy=new PotionEffect(effect.getPotionID(),Math.max(1,effect.getDuration()/8),effect.getAmplifier(),effect.getIsAmbient());copy.setCurativeItems(effect.getCurativeItems());result.add(copy);}return result;
 }
 public static void addCreative(Item item,List<ItemStack> entries){List<ItemStack> values=new ArrayList<ItemStack>();KitEffects.addPotions(item,values);for(int i=0;i<values.size()/2;i++)entries.add(values.get(i));}
 public static String displayName(ItemStack stack){if(stack==null||!stack.hasTagCompound())return null;String name=stack.getTagCompound().getString("MCMPotion");boolean known=name.equals("slowness")||name.equals("turtle_master");for(String value:KitEffects.NAMES)if(value.equals(name))known=true;return known?net.minecraft.util.StatCollector.translateToLocal("item.moderncombatmovement.arrow."+name):null;}
 public static boolean ammo(ItemStack stack){return stack!=null&&stack.stackSize>0&&(stack.getItem()==Items.arrow||stack.getItem()==tipped||stack.getItem()==spectral);}
 private static ItemStack find(EntityPlayer player){
  ItemStack off=Offhands.get(player);if(ammo(off))return off;
  ItemStack main=player.inventory.getCurrentItem();if(ammo(main))return main;
  for(int i=0;i<36;i++){ItemStack stack=player.inventory.getStackInSlot(i);if(ammo(stack))return stack;}return null;
 }
 public static void resolveGlowing(){if(Content.uab)try{Object effect=Class.forName("ganymedes01.etfuturum.potion.ModPotions").getField("glowing").get(null);glowing=effect instanceof Potion?(Potion)effect:null;}catch(ReflectiveOperationException absent){throw new IllegalStateException("Unsupported UAB glowing effect",absent);}}
 public static void register(){
  if(Content.uab){tipped=GameRegistry.findItem("etfuturum","tipped_arrow");spectral=GameRegistry.findItem("etfuturum","spectral_arrow");}
  else{
   tipped=new TippedArrowItem();spectral=new Item().setUnlocalizedName("moderncombatmovement.spectral_arrow").setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
   spectral.setTextureName("moderncombatmovement:spectral_arrow");
   GameRegistry.registerItem(tipped,"tipped_arrow");GameRegistry.registerItem(spectral,"spectral_arrow");Content.ITEMS.put("tipped_arrow",tipped);Content.ITEMS.put("spectral_arrow",spectral);
   net.minecraft.block.BlockDispenser.dispenseBehaviorRegistry.putObject(spectral,new DispenserBehaviourAmmunition());
  }
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean owns(ItemStack bow,World world){return bow!=null&&bow.getItem()==Items.bow&&Settings.projectiles(world.isRemote);}
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.LOWEST) public void nock(ArrowNockEvent event){
   if(!owns(event.result,event.entityPlayer.worldObj)||event.isCanceled())return;
   if(event.entityPlayer.capabilities.isCreativeMode||find(event.entityPlayer)!=null){event.entityPlayer.setItemInUse(event.result,event.result.getMaxItemUseDuration());event.setCanceled(true);}
  }
  @SubscribeEvent(priority=EventPriority.LOWEST) public void loose(ArrowLooseEvent event){
   if(!owns(event.bow,event.entityPlayer.worldObj)||event.isCanceled())return;
   EntityPlayer p=event.entityPlayer;ItemStack source=find(p);boolean creative=p.capabilities.isCreativeMode;
   if(source==null&&!creative)return;
   float charge=event.charge/20F;charge=(charge*charge+charge*2)/3;
   if(charge<.1F)return;if(charge>1)charge=1;
   event.setCanceled(true);if(p.worldObj.isRemote)return;
   ItemStack saved=source==null?new ItemStack(Items.arrow):source.copy();saved.stackSize=1;
   boolean infinite=creative||saved.getItem()==Items.arrow&&EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId,event.bow)>0;
   CrossbowBolt arrow=new CrossbowBolt(p.worldObj,p,saved,charge,infinite);
   int power=EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId,event.bow);if(power>0)arrow.setDamage(arrow.getDamage()+power*.5+.5);
   int punch=EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId,event.bow);if(punch>0)arrow.setKnockbackStrength(punch);
   if(EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId,event.bow)>0)arrow.setFire(100);
   if(!p.worldObj.spawnEntityInWorld(arrow))return;
   event.bow.damageItem(1,p);p.worldObj.playSoundAtEntity(p,"random.bow",1,1/(p.getRNG().nextFloat()*.4F+1.2F)+charge*.5F);
   if(!infinite&&source!=null){--source.stackSize;if(source.stackSize<=0&&source==Offhands.get(p))Offhands.set(p,null);p.inventory.markDirty();}
   p.addStat(net.minecraft.stats.StatList.objectUseStats[Item.getIdFromItem(event.bow.getItem())],1);
  }
 }
}
