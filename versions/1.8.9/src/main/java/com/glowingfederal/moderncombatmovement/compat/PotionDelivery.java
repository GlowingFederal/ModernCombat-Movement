package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import java.util.*;
/** Native legacy potion contents are preserved verbatim through every delivery form. */
public final class PotionDelivery {
 public static Item lingering;
 private PotionDelivery(){}
 public static ItemStack use(ItemStack stack,net.minecraft.world.World world,net.minecraft.entity.player.EntityPlayer player){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(world.isRemote))return stack;
  if(!world.isRemote&&!world.spawnEntityInWorld(new LingeringProjectile(world,player,stack)))return stack;
  if(!player.capabilities.isCreativeMode)--stack.stackSize;
  world.playSoundAtEntity(player,"random.bow",.5F,.4F/(player.getRNG().nextFloat()*.4F+.8F));return stack;
 }
 public static List<PotionEffect> effects(ItemStack stack){List<PotionEffect> effects=stack==null?null:net.minecraft.init.Items.potionitem.getEffects(stack);return effects==null?Collections.<PotionEffect>emptyList():effects;}
 /** UAB's displayed lingering effects, scaled from the raw saved contents exactly once. */
 public static List<PotionEffect> lingeringEffects(ItemStack stack){
  List<PotionEffect> result=new ArrayList<PotionEffect>();for(PotionEffect effect:effects(stack)){
   PotionEffect copy=Potion.potionTypes[effect.getPotionID()].isInstant()?new PotionEffect(effect):new PotionEffect(effect.getPotionID(),Math.max(1,effect.getDuration()/4),effect.getAmplifier(),effect.getIsAmbient(),effect.getIsShowParticles());
   copy.setCurativeItems(effect.getCurativeItems());result.add(copy);
  }return result;
 }
 /** Display names adapt UAB's effect-derived potion naming without serializing a client language into saved NBT. */
 public static String displayName(ItemStack stack){
  if(stack==null||!stack.hasTagCompound())return null;boolean lingeringItem=stack.getItem()==lingering;
  if(stack.getItem()!=net.minecraft.init.Items.potionitem&&!lingeringItem)return null;
  String name=stack.getTagCompound().getString("MCMPotion");
  if(name.isEmpty()){List<PotionEffect> effects=effects(stack);if(effects.size()==1)for(int i=0;i<KitEffects.EFFECTS.length;i++)if(KitEffects.EFFECTS[i]!=null&&effects.get(0).getPotionID()==KitEffects.EFFECTS[i].id)name=KitEffects.NAMES[i];}
  boolean known=name.equals("turtle_master")||name.equals("slowness");for(String candidate:KitEffects.NAMES)if(candidate.equals(name))known=true;if(!known)return null;
  String form=lingeringItem?"lingering":ItemPotion.isSplash(stack.getItemDamage())?"splash":"drink";
  return net.minecraft.util.StatCollector.translateToLocal("item.moderncombatmovement.potion."+name+"."+form);
 }
 public static void register(){
  lingering=Content.uab?net.minecraftforge.fml.common.registry.GameRegistry.findItem("etfuturum","lingering_potion"):new LingeringItem();
  if(!Content.uab){net.minecraftforge.fml.common.registry.GameRegistry.registerItem(lingering,"lingering_potion");Content.ITEMS.put("lingering_potion",lingering);}
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(LingeringProjectile.class,"lingering_potion",6,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,10,true);
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(LingeringCloud.class,"area_effect_cloud",7,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,5,false);
 }
}
