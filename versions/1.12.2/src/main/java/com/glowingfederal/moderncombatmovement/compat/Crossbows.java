package com.glowingfederal.moderncombatmovement.compat;

import java.io.File;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.nbt.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import com.glowingfederal.moderncombatmovement.Identity;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
import com.glowingfederal.moderncombatmovement.api.*;
import com.glowingfederal.moderncombatmovement.combat.*;
import net.minecraftforge.common.config.Configuration;
/** Canonical charged-ammunition owner for this mod and explicitly selected UAB crossbows only. */
public final class Crossbows {
 public static Enchantment quickCharge,multishot,piercing;
 private Crossbows() {}
 public static boolean known(ItemStack stack) { if(stack.isEmpty()) return false; String id=CombatHooks.id(stack); return id.equals("moderncombatmovement:crossbow") || id.equals("etfuturum:crossbow"); }
 public static void register(File file) {
  if(!Content.uab) { Item crossbow=new CrossbowItem(); crossbow.setRegistryName(Identity.MOD_ID,"crossbow");net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Item.class).register(crossbow); Content.ITEMS.put("crossbow",crossbow); net.minecraftforge.fml.common.registry.GameRegistry.addShapedRecipe(new net.minecraft.util.ResourceLocation(Identity.MOD_ID,"crossbow"),null,new ItemStack(crossbow),"SIS","THT"," S ",'S',Items.STICK,'I',Items.IRON_INGOT,'T',Items.STRING,'H',Blocks.TRIPWIRE_HOOK); }
  CombatApi.register((Content.uab ? "etfuturum" : Identity.MOD_ID)+":crossbow",new ItemProperties(ItemProperties.Kind.CROSSBOW,4,1,false,true));
  Configuration config=new Configuration(file);config.load();
  quickCharge=enchantment(config,"quick_charge","quickCharge",200,3); multishot=enchantment(config,"multishot","multishot",201,1); piercing=enchantment(config,"piercing","piercing",202,4);
  if(config.hasChanged()) config.save();
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new net.minecraft.util.ResourceLocation(Identity.MOD_ID,"crossbow_bolt"),CrossbowBolt.class,"crossbow_bolt",0,ModernCombatMovement.instance,64,1,true);
 }
 private static Enchantment enchantment(Configuration config,String id,String field,int preferred,int max) {
  if(Content.uab) try { Object value=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(field).get(null); if(value instanceof Enchantment) return (Enchantment)value; } catch(ReflectiveOperationException absent) { /* Feature absent: supply only that missing enchantment. */ }
  Enchantment found=Enchantment.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("etfuturum",id));if(found!=null) return found;
  Enchantment enchantment=new CrossbowEnchant(id,max).setRegistryName(Identity.MOD_ID,id);net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Enchantment.class).register(enchantment);return enchantment;
 }
 public static int level(Enchantment enchantment,ItemStack stack) { return enchantment==null || stack.isEmpty() ? 0 : EnchantmentHelper.getEnchantmentLevel(enchantment,stack); }
 public static int chargeTime(ItemStack stack) { return CrossbowMath.chargeTicks(level(quickCharge,stack)); }
 public static int duration(ItemStack stack) { return chargeTime(stack)+3; }
 public static boolean charged(ItemStack stack) { return !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().getBoolean("Charged"); }
 private static boolean ammo(ItemStack stack) { if(stack.isEmpty()) return false; String id=CombatHooks.id(stack);return id.equals("minecraft:arrow") || id.equals("minecraft:tipped_arrow") || id.equals("minecraft:spectral_arrow") || id.equals("etfuturum:tipped_arrow") || id.equals("etfuturum:spectral_arrow"); }
 private static ItemStack ammunition(EntityPlayer p) {
  if(ammo(p.getHeldItemOffhand())) return p.getHeldItemOffhand(); if(ammo(p.getHeldItemMainhand())) return p.getHeldItemMainhand();
  for(int i=0;i<p.inventory.getSizeInventory();i++) { ItemStack stack=p.inventory.getStackInSlot(i); if(ammo(stack)) return stack; }
  return ItemStack.EMPTY;
 }
 public static boolean use(ItemStack stack,EntityPlayer p ,net.minecraft.util.EnumHand hand) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(p.world.isRemote)) return false;
  if(charged(stack)) { if(!p.world.isRemote) fire(stack,p); return true; }
  ItemStack ammunition=ammunition(p); if(ammunition.isEmpty() && !p.capabilities.isCreativeMode) return false;
  p.setActiveHand(hand); return true;
 }
 public static void tick(ItemStack stack,EntityPlayer p,int remaining) {
  if(duration(stack)-remaining>=chargeTime(stack)) { load(stack,p); p.stopActiveHand(); }
 }
 public static void release(ItemStack stack,EntityPlayer p,int remaining) { if(duration(stack)-remaining>=chargeTime(stack)) load(stack,p); }
 public static void load(ItemStack stack,EntityPlayer p) {
  if(p.world.isRemote || charged(stack)) return;
  ItemStack source=ammunition(p); if(source.isEmpty() && !p.capabilities.isCreativeMode) return;
  ItemStack saved=source.isEmpty() ? new ItemStack(Items.ARROW) : source.copy(); saved.setCount(1);
  if(!p.capabilities.isCreativeMode) source.shrink(1);
  if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
  NBTTagCompound tag=stack.getTagCompound(); NBTTagList rounds=new NBTTagList(); int count=level(multishot,stack)>0 ? 3 : 1;
  for(int i=0;i<count;i++) rounds.appendTag(saved.writeToNBT(new NBTTagCompound()));
  tag.setTag("MCMProjectiles",rounds);tag.setBoolean("Charged",true);tag.setBoolean("CreativeCharge",p.capabilities.isCreativeMode);p.inventory.markDirty();
 }
 public static void fire(ItemStack stack,EntityPlayer p) {
  if(p.world.isRemote || !charged(stack)) return;
  NBTTagCompound tag=stack.getTagCompound();NBTTagList rounds=tag.getTagList("MCMProjectiles",10);
  // Migrate already charged UAB items without taking a second arrow from the inventory.
  if(rounds.tagCount()==0) { NBTTagCompound prior=tag.hasKey("ChargedProjectile",10) ? tag.getCompoundTag("ChargedProjectile") : new ItemStack(Items.ARROW).writeToNBT(new NBTTagCompound()); int count=level(multishot,stack)>0 ? 3 : 1; for(int i=0;i<count;i++)rounds.appendTag(prior.copy()); }
  tag.setBoolean("Charged",false);tag.removeTag("MCMProjectiles");tag.removeTag("ChargedProjectile");
  int limit=Math.min(3,rounds.tagCount());
  for(int i=0;i<limit;i++) {
   ItemStack ammunition=new ItemStack(rounds.getCompoundTagAt(i)); if(!ammo(ammunition)) continue;
   CrossbowBolt bolt=new CrossbowBolt(p.world,p,ammunition,level(piercing,stack),i>0 || tag.getBoolean("CreativeCharge"));
   double[] direction=CrossbowMath.direction(p.rotationYaw,p.rotationPitch,i==0 ? 0 : i==1 ? -10 : 10);bolt.launch(direction[0],direction[1],direction[2]);
   p.world.spawnEntity(bolt);stack.damageItem(1,p);
  }
  p.inventory.markDirty();
 }
 private static final class CrossbowEnchant extends Enchantment {
  private final String key;private final int max;
  CrossbowEnchant(String key,int max) { super(Enchantment.Rarity.UNCOMMON,EnumEnchantmentType.BOW,new net.minecraft.inventory.EntityEquipmentSlot[]{net.minecraft.inventory.EntityEquipmentSlot.MAINHAND,net.minecraft.inventory.EntityEquipmentSlot.OFFHAND});this.key=key;this.max=max;setName("moderncombatmovement."+key); }
  @Override public int getMaxLevel() { return max; }
  @Override public int getMinEnchantability(int level) { return key.equals("quick_charge") ? 12+(level-1)*20 : key.equals("multishot") ? 20 : 1+(level-1)*10; }
  @Override public int getMaxEnchantability(int level) { return 50; }
  @Override public boolean canApply(ItemStack stack) { return known(stack); }
  @Override public boolean canApplyTogether(Enchantment other) { return super.canApplyTogether(other) && !(this==multishot && other==piercing || this==piercing && other==multishot); }
 }
}
