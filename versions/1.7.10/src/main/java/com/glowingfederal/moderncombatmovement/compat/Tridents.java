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
 public static boolean known(ItemStack stack){return stack!=null&&stack.getItem()==item;}
 public static boolean aquatic(net.minecraft.entity.Entity entity){return entity instanceof net.minecraft.entity.passive.EntityWaterMob||uabAquatic(entity);}
 private static boolean uabAquatic(net.minecraft.entity.Entity entity){
  for(Class<?> type=entity.getClass();type!=null;type=type.getSuperclass()){
   String name=type.getName();if(name.equals("ganymedes01.etfuturum.entities.EntityGuardian")||name.equals("ganymedes01.etfuturum.entities.EntityAquaticAnimal")||name.equals("ganymedes01.etfuturum.entities.EntityNautilus"))return true;
  }return false;
 }
 public static int level(Enchantment e,ItemStack stack){return Crossbows.level(e,stack);}
 public static void register(java.io.File file){
  if(Content.uab){UabTridentBridge bridge=new UabTridentBridge();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(bridge);cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(bridge);}
  item=Content.uab?cpw.mods.fml.common.registry.GameRegistry.findItem("etfuturum","trident"):new TridentItem();
  if(!Content.uab){cpw.mods.fml.common.registry.GameRegistry.registerItem(item,"trident");Content.ITEMS.put("trident",item);}
  Configuration c=new Configuration(file);c.load();loyalty=create(c,"loyalty",204,3);impaling=create(c,"impaling",205,5);riptide=create(c,"riptide",206,3);channeling=create(c,"channeling",207,1);if(c.hasChanged())c.save();
  com.glowingfederal.moderncombatmovement.api.CombatApi.register((Content.uab?"etfuturum":"moderncombatmovement")+":trident",new com.glowingfederal.moderncombatmovement.api.ItemProperties(com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.TRIDENT,1.1,9,false,true));
  cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(TridentProjectile.class,"trident",4,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,1,true);
  if(item!=null)com.glowingfederal.moderncombatmovement.offhand.OffhandUses.register(item,new HandAdapter());
 }
 private static Enchantment create(Configuration c,String key,int preferred,int max){
  if(Content.uab)try{Object value=Class.forName("ganymedes01.etfuturum.ModEnchantments").getField(key).get(null);if(value instanceof Enchantment)return(Enchantment)value;}catch(ReflectiveOperationException missing){}
  int id=c.get("Compatibility",key+"EnchantmentId",preferred,"Must be free and match on both sides.").getInt();if(id<0||id>255||Enchantment.enchantmentsList[id]!=null)throw new IllegalStateException("Occupied/out of range "+key+" enchantment ID "+id);Enchantment e=new TridentEnchant(id,key,max);Enchantment.addToBookList(e);return e;
 }
 public static boolean use(ItemStack stack,EntityPlayer p ){
  if(!Settings.projectiles(p.worldObj.isRemote)||stack.getItemDamage()>=stack.getMaxDamage()-1||level(riptide,stack)>0&&!p.isWet()||Content.uab&&!UabContent.enabled("tridents"))return false;
  p.setItemInUse(stack,72000); return true;
 }
 public static void release(ItemStack stack,EntityPlayer p,int remaining){
  if(p.worldObj.isRemote||!Settings.projectiles(false)||72000-remaining<10||stack.getItemDamage()>=stack.getMaxDamage()-1)return;
  int spin=level(riptide,stack);if(spin>0){if(!p.isWet())return;net.minecraft.util.Vec3 look=p.getLookVec();float speed=1.5F+.75F*(spin-1);p.addVelocity(look.xCoord*speed,look.yCoord*speed,look.zCoord*speed);if(p.onGround)p.moveEntity(0,1.1999999F,0);p.velocityChanged=true;stack.damageItem(1,p);startSpin(p,stack);used(p,stack);ItemSounds.play(p,"item.trident.riptide_"+Math.min(3,spin),1,1);return;}
  ItemStack thrown=stack.copy();thrown.stackSize=1; thrown.damageItem(1,p);TridentProjectile projectile=new TridentProjectile(p.worldObj,p,thrown);projectile.creative(p.capabilities.isCreativeMode);projectile.launch(p);
  if(p.worldObj.spawnEntityInWorld(projectile)){if(!p.capabilities.isCreativeMode)--stack.stackSize;p.inventory.markDirty();used(p,stack);ItemSounds.play(p,"item.trident.throw",1,1);}
 }
 private static final class HandAdapter implements com.glowingfederal.moderncombatmovement.offhand.OffhandUses.Adapter {public ItemStack use(ItemStack stack,net.minecraft.world.World w,EntityPlayer p){Tridents.use(stack,p);return stack;}public boolean useOnBlock(ItemStack stack,EntityPlayer p,int x,int y,int z,int side,float hx,float hy,float hz){return false;}}

 /** Modern auto-spin contact rules, retaining the actual launching hand stack. */
 public static boolean spinning(EntityPlayer p){return com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinTicks>0;}
 private static void startSpin(EntityPlayer p,ItemStack stack){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  s.spinTicks=20;s.spinWeapon=stack;s.spinBox=null;
  com.glowingfederal.moderncombatmovement.pose.PlayerHooks.update(p);
 }
 public static void beforeSpin(EntityPlayer p){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  if(!p.isEntityAlive()||!Settings.projectiles(p.worldObj.isRemote)){s.spinTicks=0;s.spinWeapon=null;}
  s.spinBox=s.spinTicks>0?p.boundingBox.copy():null;
 }
 public static void afterSpin(EntityPlayer p){
  com.glowingfederal.moderncombatmovement.pose.PlayerState s=com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p);
  if(s.spinTicks<=0)return;
  --s.spinTicks;
  if(!p.worldObj.isRemote&&s.spinBox!=null){
   net.minecraft.util.AxisAlignedBB old=(net.minecraft.util.AxisAlignedBB)s.spinBox,now=p.boundingBox;
   net.minecraft.util.AxisAlignedBB swept=net.minecraft.util.AxisAlignedBB.getBoundingBox(Math.min(old.minX,now.minX),Math.min(old.minY,now.minY),Math.min(old.minZ,now.minZ),Math.max(old.maxX,now.maxX),Math.max(old.maxY,now.maxY),Math.max(old.maxZ,now.maxZ));
   java.util.List entities=p.worldObj.getEntitiesWithinAABBExcludingEntity(p,swept);
   if(!entities.isEmpty()){
    for(Object value:entities)if(value instanceof net.minecraft.entity.EntityLivingBase){
     net.minecraft.entity.EntityLivingBase target=(net.minecraft.entity.EntityLivingBase)value;
     s.spinAttacking=true;
     try{com.glowingfederal.moderncombatmovement.combat.LegacyAttack.spin(p,target,(ItemStack)s.spinWeapon);}finally{s.spinAttacking=false;}
     s.spinTicks=0;p.motionX*=-.2;p.motionY*=-.2;p.motionZ*=-.2;p.velocityChanged=true;break;
    }
   }else if(p.isCollidedHorizontally)s.spinTicks=0;
  }
  s.spinBox=null;
  if(s.spinTicks<=0){s.spinWeapon=null;com.glowingfederal.moderncombatmovement.pose.PlayerHooks.update(p);}
 }
 private static void used(EntityPlayer p,ItemStack stack){p.addStat(net.minecraft.stats.StatList.objectUseStats[Item.getIdFromItem(stack.getItem())],1);}
 private static final class TridentEnchant extends Enchantment {
  private final String key;private final int max;
  TridentEnchant(int id,String key,int max){super(id,key.equals("loyalty")?5:key.equals("channeling")?1:2,EnumEnchantmentType.all); this.key=key;this.max=max;setName("moderncombatmovement."+key);}
  @Override public int getMaxLevel(){return max;}
  @Override public int getMinEnchantability(int level){return key.equals("loyalty")?12+(level-1)*7:key.equals("riptide")?17+(level-1)*7:key.equals("impaling")?1+(level-1)*8:25;}
  @Override public int getMaxEnchantability(int level){return key.equals("impaling")?21+(level-1)*8:50;}

  @Override public boolean canApply(ItemStack stack){return known(stack);}
  @Override public boolean canApplyTogether(Enchantment other){return super.canApplyTogether(other)&&!(this==riptide&&(other==loyalty||other==channeling)||(this==loyalty||this==channeling)&&other==riptide);}
 }
}
