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
 public static boolean known(ItemStack stack) { if(stack==null) return false; String id=CombatHooks.id(stack); return id.equals("moderncombatmovement:crossbow") || id.equals("etfuturum:crossbow"); }
 public static void register(File file) {
  if(!Content.uab) { Item crossbow=new CrossbowItem(); net.minecraftforge.fml.common.registry.GameRegistry.registerItem(crossbow,"crossbow"); Content.ITEMS.put("crossbow",crossbow); net.minecraftforge.fml.common.registry.GameRegistry.addRecipe(new ItemStack(crossbow),"SIS","THT"," S ",'S',Items.stick,'I',Items.iron_ingot,'T',Items.string,'H',Blocks.tripwire_hook); }
  CombatApi.register((Content.uab ? "etfuturum" : Identity.MOD_ID)+":crossbow",new ItemProperties(ItemProperties.Kind.CROSSBOW,4,1,false,true));
  Configuration config=new Configuration(file);config.load();
  quickCharge=enchantment(config,"quick_charge","quickCharge",200,3); multishot=enchantment(config,"multishot","multishot",201,1); piercing=enchantment(config,"piercing","piercing",202,4);
  if(config.hasChanged()) config.save();
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(CrossbowBolt.class,"crossbow_bolt",0,ModernCombatMovement.instance,64,1,true);
 }
 private static Enchantment enchantment(Configuration config,String id,String field,int preferred,int max) {
  if(Content.uab) try { Object value=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(field).get(null); if(value instanceof Enchantment) return (Enchantment)value; } catch(ReflectiveOperationException absent) { /* Feature absent: supply only that missing enchantment. */ }
  int allocated=config.get("Compatibility",id+"EnchantmentId",preferred,"Legacy enchantment ID; must be free and match on both sides.").getInt();
  if(allocated<0 || allocated>255 || Enchantment.getEnchantmentById(allocated)!=null) throw new IllegalStateException("Occupied/out of range enchantment ID: "+allocated);
  Enchantment enchantment=new CrossbowEnchant(allocated,id,max);Enchantment.addToBookList(enchantment);return enchantment;
 }
 public static int level(Enchantment enchantment,ItemStack stack) { return enchantment==null || stack==null ? 0 : EnchantmentHelper.getEnchantmentLevel(enchantment.effectId,stack); }
 public static int chargeTime(ItemStack stack) { return CrossbowMath.chargeTicks(level(quickCharge,stack)); }
 public static int duration(ItemStack stack) { return 72000; }
 public static boolean charged(ItemStack stack) { return stack!=null && stack.hasTagCompound() && stack.getTagCompound().getBoolean("Charged"); }
 public static boolean firework(ItemStack stack){
  if(!charged(stack))return false;NBTTagCompound tag=stack.getTagCompound();NBTTagList rounds=tag.getTagList("MCMProjectiles",10);
  NBTTagCompound saved=rounds.tagCount()>0?rounds.getCompoundTagAt(0):tag.getCompoundTag("ChargedProjectile");
  return saved.getString("id").equals("minecraft:fireworks");
 }
 private static boolean ammo(ItemStack stack) { if(stack==null) return false; String id=CombatHooks.id(stack);return id.equals("minecraft:fireworks") || id.equals("minecraft:arrow") || id.equals("moderncombatmovement:tipped_arrow") || id.equals("moderncombatmovement:spectral_arrow") || id.equals("minecraft:tipped_arrow") || id.equals("minecraft:spectral_arrow") || id.equals("etfuturum:tipped_arrow") || id.equals("etfuturum:spectral_arrow"); }
 private static ItemStack ammunition(EntityPlayer p) {
  ItemStack hand=com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p);if(ammo(hand)&&hand.stackSize>0)return hand;
  hand=p.inventory.getCurrentItem();if(ammo(hand)&&hand.stackSize>0)return hand;

  for(int i=0;i<p.inventory.getSizeInventory();i++) { ItemStack stack=p.inventory.getStackInSlot(i); if(ammo(stack)&&!CombatHooks.id(stack).equals("minecraft:fireworks")) return stack; }
  return null;
 }
 public static boolean use(ItemStack stack,EntityPlayer p ) {
  if(!com.glowingfederal.moderncombatmovement.config.Settings.projectiles(p.worldObj.isRemote)) return false;
  if(charged(stack)) { if(!p.worldObj.isRemote) fire(stack,p); return true; }
  ItemStack ammunition=ammunition(p); if(ammunition==null && !p.capabilities.isCreativeMode) return false;
  p.setItemInUse(stack,duration(stack)); return true;
 }
 public static void tick(ItemStack stack,EntityPlayer p,int remaining) {
  int elapsed=duration(stack)-remaining,time=chargeTime(stack),quick=level(quickCharge,stack);
  if(elapsed==(int)Math.ceil(time*.2))ItemSounds.play(p,quick>0?"item.crossbow.quick_charge_"+Math.min(3,quick):"item.crossbow.loading_start",.5F,1);
  if(quick==0&&elapsed==(int)Math.ceil(time*.5))ItemSounds.play(p,"item.crossbow.loading_middle",.5F,1);
  if(elapsed>=time)load(stack,p);
 }
 public static void release(ItemStack stack,EntityPlayer p,int remaining) { if(duration(stack)-remaining>=chargeTime(stack)) load(stack,p); }
 public static void load(ItemStack stack,EntityPlayer p) {
  if(p.worldObj.isRemote || charged(stack)) return;
  ItemStack source=ammunition(p); if(source==null && !p.capabilities.isCreativeMode) return;
  ItemStack saved=source==null ? new ItemStack(Items.arrow) : source.copy(); saved.stackSize=1;
  if(!p.capabilities.isCreativeMode){--source.stackSize;if(source.stackSize<=0&&source==com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p))com.glowingfederal.moderncombatmovement.offhand.Offhands.set(p,null);}
  if(source!=null&&source.stackSize<=0)for(int slot=0;slot<p.inventory.getSizeInventory();slot++)if(p.inventory.getStackInSlot(slot)==source)p.inventory.setInventorySlotContents(slot,null);
  if(source!=null&&source.stackSize<=0){for(int i=0;i<p.inventory.getSizeInventory();i++)if(p.inventory.getStackInSlot(i)==source)p.inventory.setInventorySlotContents(i,null);}
  if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
  NBTTagCompound tag=stack.getTagCompound(); NBTTagList rounds=new NBTTagList(); int count=level(multishot,stack)>0 ? 3 : 1;
  for(int i=0;i<count;i++) rounds.appendTag(saved.writeToNBT(new NBTTagCompound()));
  ItemSounds.play(p,"item.crossbow.loading_end",1,1/(p.getRNG().nextFloat()*.5F+1)+.2F);tag.setTag("MCMProjectiles",rounds);tag.setBoolean("Charged",true);tag.setBoolean("CreativeCharge",p.capabilities.isCreativeMode);p.inventory.markDirty();
 }
 public static void fire(ItemStack stack,EntityPlayer p) {
  if(p.worldObj.isRemote || !charged(stack)) return;
  NBTTagCompound tag=stack.getTagCompound();NBTTagList rounds=tag.getTagList("MCMProjectiles",10);
  // Migrate already charged UAB items without taking a second arrow from the inventory.
  if(rounds.tagCount()==0) { NBTTagCompound prior=tag.hasKey("ChargedProjectile",10) ? tag.getCompoundTag("ChargedProjectile") : new ItemStack(Items.arrow).writeToNBT(new NBTTagCompound()); int count=level(multishot,stack)>0 ? 3 : 1; for(int i=0;i<count;i++)rounds.appendTag(prior.copy()); }
  tag.setBoolean("Charged",false);tag.removeTag("MCMProjectiles");tag.removeTag("ChargedProjectile");
  int limit=Math.min(3,rounds.tagCount());
  for(int i=0;i<limit;i++) {
   ItemStack ammunition=ItemStack.loadItemStackFromNBT(rounds.getCompoundTagAt(i)); if(!ammo(ammunition)) continue;
   ItemSounds.play(p,"item.crossbow.shoot",1,i==0?1:1/(p.getRNG().nextFloat()*.5F+1.8F)+(i==1?.63F:.43F));
   double[] direction=CrossbowMath.direction(p.rotationYaw,p.rotationPitch,i==0 ? 0 : i==1 ? -10 : 10);
   if(CombatHooks.id(ammunition).equals("minecraft:fireworks")){if(p.worldObj.spawnEntityInWorld(BoostRocket.crossbow(p.worldObj,p,ammunition,direction)))stack.damageItem(3,p);continue;}
   CrossbowBolt bolt=new CrossbowBolt(p.worldObj,p,ammunition,level(piercing,stack),i>0 || tag.getBoolean("CreativeCharge"));
   bolt.launch(direction[0],direction[1],direction[2]);
   if(p.worldObj.spawnEntityInWorld(bolt))stack.damageItem(1,p);
  }
  p.addStat(net.minecraft.stats.StatList.objectUseStats[Item.getIdFromItem(stack.getItem())],1);
  p.inventory.markDirty();
 }
 private static final class CrossbowEnchant extends Enchantment {
  private final String key;private final int max;
  CrossbowEnchant(int id,String key,int max) { super(id,new net.minecraft.util.ResourceLocation("moderncombatmovement:"+key),2,EnumEnchantmentType.BOW);this.key=key;this.max=max;setName("moderncombatmovement."+key); }
  @Override public int getMaxLevel() { return max; }
  @Override public int getMinEnchantability(int level) { return key.equals("quick_charge") ? 12+(level-1)*20 : key.equals("multishot") ? 20 : 1+(level-1)*10; }
  @Override public int getMaxEnchantability(int level) { return 50; }
  @Override public boolean canApply(ItemStack stack) { return known(stack); }
  @Override public boolean canApplyTogether(Enchantment other) { return super.canApplyTogether(other) && !(this==multishot && other==piercing || this==piercing && other==multishot); }
 }
}
