package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.enchantment.*;
import net.minecraftforge.common.config.Configuration;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class Tridents {
 public static Enchantment loyalty,impaling,riptide,channeling;
 private static Item item;
 private Tridents(){}
 public static boolean known(ItemStack stack){return !stack.isEmpty()&&(com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack).equals("moderncombatmovement:trident")||com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack).equals("etfuturum:trident"));}
 public static boolean aquatic(net.minecraft.entity.Entity entity){return entity instanceof net.minecraft.entity.passive.EntityWaterMob||entity instanceof net.minecraft.entity.monster.EntityGuardian;}
 public static int level(Enchantment e,ItemStack stack){return Crossbows.level(e,stack);}
 public static void register(java.io.File file){
  if(Content.uab){UabTridentBridge bridge=new UabTridentBridge();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(bridge);net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(bridge);}
  item=Content.uab?net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("etfuturum:trident")):new TridentItem();
  if(!Content.uab){item.setRegistryName("moderncombatmovement","trident");net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Item.class).register(item);Content.ITEMS.put("trident",item);}
  Configuration c=new Configuration(file);c.load();loyalty=create(c,"loyalty",204,3);impaling=create(c,"impaling",205,5);riptide=create(c,"riptide",206,3);channeling=create(c,"channeling",207,1);if(c.hasChanged())c.save();
  com.glowingfederal.moderncombatmovement.api.CombatApi.register((Content.uab?"etfuturum":"moderncombatmovement")+":trident",new com.glowingfederal.moderncombatmovement.api.ItemProperties(com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.TRIDENT,1.1,9,false,true));
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new net.minecraft.util.ResourceLocation("moderncombatmovement:trident"),TridentProjectile.class,"trident",4,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,1,true);
  
 }
 private static Enchantment create(Configuration c,String key,int preferred,int max){
  if(Content.uab)try{Object value=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(key).get(null);if(value instanceof Enchantment)return(Enchantment)value;}catch(ReflectiveOperationException missing){}
  Enchantment e=new TridentEnchant(key,max).setRegistryName("moderncombatmovement",key);net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Enchantment.class).register(e);return e;
 }
 public static boolean use(ItemStack stack,EntityPlayer p ,net.minecraft.util.EnumHand hand){
  if(!Settings.projectiles(p.world.isRemote)||stack.getItemDamage()>=stack.getMaxDamage()-1||level(riptide,stack)>0&&!p.isWet()||Content.uab&&!UabContent.enabled("tridents"))return false;
  p.setActiveHand(hand); return true;
 }
 public static void release(ItemStack stack,EntityPlayer p,int remaining){
  if(p.world.isRemote||!Settings.projectiles(false)||72000-remaining<10||stack.getItemDamage()>=stack.getMaxDamage()-1)return;
  int spin=level(riptide,stack);if(spin>0){if(!p.isWet())return;net.minecraft.util.math.Vec3d look=p.getLookVec();float speed=1.5F+.75F*(spin-1);p.addVelocity(look.x*speed,look.y*speed,look.z*speed);if(p.onGround)p.move(net.minecraft.entity.MoverType.SELF,0,1.1999999F,0);p.velocityChanged=true;stack.damageItem(1,p);startSpin(p,stack);used(p,stack);ItemSounds.play(p,"item.trident.riptide_"+Math.min(3,spin),1,1);return;}
  ItemStack thrown=stack.copy();thrown.setCount(1); thrown.damageItem(1,p);TridentProjectile projectile=new TridentProjectile(p.world,p,thrown);projectile.creative(p.capabilities.isCreativeMode);projectile.launch(p);
  if(p.world.spawnEntity(projectile)){if(!p.capabilities.isCreativeMode)stack.shrink(1);p.inventory.markDirty();used(p,stack);ItemSounds.play(p,"item.trident.throw",1,1);}
 }

 /** Modern auto-spin contact rules, retaining the actual launching hand stack. */
 public static boolean spinning(EntityPlayer p){return com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinTicks>0;}
 private static void startSpin(EntityPlayer p,ItemStack stack){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  s.spinTicks=20;s.spinWeapon=stack;s.spinBox=null;s.spinOffhand=p.getHeldItemOffhand()==stack;
  com.glowingfederal.moderncombatmovement.pose.PlayerHooks.update(p);
 }
 public static void beforeSpin(EntityPlayer p){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  if(!p.isEntityAlive()||!Settings.projectiles(p.world.isRemote)){s.spinTicks=0;s.spinWeapon=null;}
  s.spinBox=s.spinTicks>0?p.getEntityBoundingBox():null;
 }
 public static void afterSpin(EntityPlayer p){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  if(s.spinTicks<=0)return;
  --s.spinTicks;
  if(!p.world.isRemote&&s.spinBox!=null){
   net.minecraft.util.math.AxisAlignedBB old=(net.minecraft.util.math.AxisAlignedBB)s.spinBox,now=p.getEntityBoundingBox();
   net.minecraft.util.math.AxisAlignedBB swept=new net.minecraft.util.math.AxisAlignedBB(Math.min(old.minX,now.minX),Math.min(old.minY,now.minY),Math.min(old.minZ,now.minZ),Math.max(old.maxX,now.maxX),Math.max(old.maxY,now.maxY),Math.max(old.maxZ,now.maxZ));
   java.util.List entities=p.world.getEntitiesWithinAABBExcludingEntity(p,swept);
   if(!entities.isEmpty()){
    for(Object value:entities)if(value instanceof net.minecraft.entity.EntityLivingBase){
     net.minecraft.entity.EntityLivingBase target=(net.minecraft.entity.EntityLivingBase)value;
     s.spinAttacking=true;
     try{p.attackTargetEntityWithCurrentItem(target);}finally{s.spinAttacking=false;}
     s.spinTicks=0;p.motionX*=-.2;p.motionY*=-.2;p.motionZ*=-.2;p.velocityChanged=true;break;
    }
   }else if(p.collidedHorizontally)s.spinTicks=0;
  }
  s.spinBox=null;
  if(s.spinTicks<=0){s.spinWeapon=null;com.glowingfederal.moderncombatmovement.pose.PlayerHooks.update(p);}
 }
 private static void used(EntityPlayer p,ItemStack stack){p.addStat(net.minecraft.stats.StatList.getObjectUseStats(stack.getItem()));}
 private static final class TridentEnchant extends Enchantment {
  private final String key;private final int max;
  TridentEnchant(String key,int max){super(key.equals("loyalty")?Rarity.UNCOMMON:key.equals("channeling")?Rarity.VERY_RARE:Rarity.RARE,EnumEnchantmentType.ALL,new net.minecraft.inventory.EntityEquipmentSlot[]{net.minecraft.inventory.EntityEquipmentSlot.MAINHAND,net.minecraft.inventory.EntityEquipmentSlot.OFFHAND}); this.key=key;this.max=max;setName("moderncombatmovement."+key);}
  @Override public int getMaxLevel(){return max;}
  @Override public int getMinEnchantability(int level){return key.equals("loyalty")?12+(level-1)*7:key.equals("riptide")?17+(level-1)*7:key.equals("impaling")?1+(level-1)*8:25;}
  @Override public int getMaxEnchantability(int level){return key.equals("impaling")?21+(level-1)*8:50;}

  @Override public boolean canApply(ItemStack stack){return known(stack);}
  @Override public boolean canApplyTogether(Enchantment other){return super.canApplyTogether(other)&&!(this==riptide&&(other==loyalty||other==channeling)||(this==loyalty||this==channeling)&&other==riptide);}
 }
}
