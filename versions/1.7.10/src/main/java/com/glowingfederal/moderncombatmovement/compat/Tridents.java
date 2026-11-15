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
 public static int level(Enchantment e,ItemStack stack){return Crossbows.level(e,stack);}
 public static void register(java.io.File file){
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
  int spin=level(riptide,stack);if(spin>0){if(!p.isWet())return;net.minecraft.util.Vec3 look=p.getLookVec();float speed=1.5F+.75F*(spin-1);p.addVelocity(look.xCoord*speed,look.yCoord*speed,look.zCoord*speed);if(p.onGround)p.moveEntity(0,1.1999999F,0);p.velocityChanged=true;stack.damageItem(1,p);return;}
  ItemStack thrown=stack.copy();thrown.stackSize=1; thrown.damageItem(1,p);TridentProjectile projectile=new TridentProjectile(p.worldObj,p,thrown);projectile.creative(p.capabilities.isCreativeMode);
  if(p.worldObj.spawnEntityInWorld(projectile)){if(!p.capabilities.isCreativeMode)--stack.stackSize;p.inventory.markDirty();}
 }
 private static final class HandAdapter implements com.glowingfederal.moderncombatmovement.offhand.OffhandUses.Adapter {public ItemStack use(ItemStack stack,net.minecraft.world.World w,EntityPlayer p){Tridents.use(stack,p);return stack;}public boolean useOnBlock(ItemStack stack,EntityPlayer p,int x,int y,int z,int side,float hx,float hy,float hz){return false;}}
 private static final class TridentEnchant extends Enchantment {
  private final String key;private final int max;
  TridentEnchant(int id,String key,int max){super(id,2,EnumEnchantmentType.all); this.key=key;this.max=max;setName("moderncombatmovement."+key);}
  @Override public int getMaxLevel(){return max;}
  @Override public boolean canApply(ItemStack stack){return known(stack);}
  @Override public boolean canApplyTogether(Enchantment other){return super.canApplyTogether(other)&&!(this==riptide&&(other==loyalty||other==channeling)||(this==loyalty||this==channeling)&&other==riptide);}
 }
}
