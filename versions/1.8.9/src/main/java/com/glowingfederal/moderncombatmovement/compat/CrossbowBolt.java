package com.glowingfederal.moderncombatmovement.compat;

import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.*;
import net.minecraft.init.Items;
import net.minecraft.nbt.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
/** Native arrow lifecycle with owned crossbow launch, ammunition and same-tick piercing adaptation. */
public final class CrossbowBolt extends EntityArrow implements IEntityAdditionalSpawnData {
 private ItemStack ammunition=new ItemStack(Items.arrow);
 private int piercing;
 private boolean nativeStep,nativeSuccess;
 private final Set<UUID> struck=new HashSet<UUID>();
 public CrossbowBolt(World world) { super(world); }
 public CrossbowBolt(World world,EntityPlayer shooter,ItemStack ammo,int piercing,boolean creative) {
  super(world,shooter,1F);
  ammunition=ammo.copy();this.piercing=Math.max(0,Math.min(4,piercing));setDamage(2);setIsCritical(true);canBePickedUp=creative?2:1;
 }
 public int piercing() { return piercing; }
 public void launch(double x,double y,double z) {
  double length=Math.sqrt(x*x+y*y+z*z);
  motionX=(x/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*3.15F;
  motionY=(y/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*3.15F;
  motionZ=(z/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*3.15F;
  rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);
  rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
 }
 public boolean mayHit(Entity target) { return piercing<=0 || !struck.contains(target.getUniqueID()) && struck.size()<piercing+1; }
 public List<Entity> filter(List<Entity> entities) { if(piercing<=0)return entities;List<Entity> result=new ArrayList<Entity>();for(Entity e:entities)if(!struck.contains(e.getUniqueID()))result.add(e);return result; }
 public void record(Entity target,boolean success) { nativeSuccess=success; if(piercing>0)struck.add(target.getUniqueID());if(success && target instanceof EntityLivingBase && !worldObj.isRemote) applyAmmunition((EntityLivingBase)target); }
 public void discard() { nativeStep=false;super.setDead(); }
 @Override public void setDead() { if(!(nativeStep && nativeSuccess && piercing>0))super.setDead(); }
 @Override public void onUpdate() {
  double x=posX,y=posY,z=posZ,vx=motionX,vy=motionY,vz=motionZ;
  List<Hit> remaining=piecingHits(x,y,z,vx,vy,vz);
  nativeSuccess=false;nativeStep=true;
  try { super.onUpdate(); } finally { nativeStep=false; }
  if(worldObj.isRemote || isDead || piercing<=0 || !nativeSuccess) return;
  double fx=posX,fy=posY,fz=posZ,dx=motionX,dy=motionY,dz=motionZ;
  try {
   motionX=vx;motionY=vy;motionZ=vz;
   for(Hit hit:remaining) {
    if(struck.contains(hit.entity.getUniqueID()))continue;
    if(!mayHit(hit.entity)) { discard();break; }
    setPosition(hit.x,hit.y,hit.z);
    float damage=(float)Math.ceil(Math.min(2147483647D,Math.sqrt(vx*vx+vy*vy+vz*vz)*getDamage()));
    if(getIsCritical()) damage+=rand.nextInt((int)damage/2+2);
    boolean burning=hit.entity.isBurning();if(isBurning() && !(hit.entity instanceof net.minecraft.entity.monster.EntityEnderman))hit.entity.setFire(5);
    boolean success=hit.entity.attackEntityFrom(DamageSource.causeArrowDamage(this,shootingEntity==null?this:shootingEntity),damage);
    record(hit.entity,success);
    if(!success) { if(!burning)hit.entity.extinguish();motionX=-vx*.1;motionY=-vy*.1;motionZ=-vz*.1;setIsCritical(false);dx=motionX;dy=motionY;dz=motionZ;break; }
    if(hit.entity instanceof EntityLivingBase) { if(shootingEntity instanceof EntityLivingBase) net.minecraft.enchantment.EnchantmentHelper.applyThornEnchantments((EntityLivingBase)hit.entity,shootingEntity); }
   }
  } finally { if(!isDead)setPosition(fx,fy,fz);motionX=dx;motionY=dy;motionZ=dz; }
 }
 private List<Hit> piecingHits(double x,double y,double z,double vx,double vy,double vz) {
  List<Hit> hits=new ArrayList<Hit>();if(piercing<=0 || worldObj.isRemote)return hits;
  Vec3 start=new Vec3(x,y,z),end=new Vec3(x+vx,y+vy,z+vz);
  MovingObjectPosition block=worldObj.rayTraceBlocks(start,end,false,true,false);if(block!=null)end=block.hitVec;
  for(Object object:worldObj.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().addCoord(vx,vy,vz).expand(1,1,1))) {
   Entity entity=(Entity)object;if(!entity.canBeCollidedWith() || entity.isDead || entity==shootingEntity && ticksExisted<5 || struck.contains(entity.getUniqueID()))continue;
   MovingObjectPosition hit=entity.getEntityBoundingBox().expand(.3,.3,.3).calculateIntercept(start,end);if(hit==null)continue;
   double hx=hit.hitVec.xCoord,hy=hit.hitVec.yCoord,hz=hit.hitVec.zCoord;
   hits.add(new Hit(entity,hx,hy,hz,(hx-x)*(hx-x)+(hy-y)*(hy-y)+(hz-z)*(hz-z)));
  }
  Collections.sort(hits,new Comparator<Hit>() { public int compare(Hit a,Hit b) { return Double.compare(a.distance,b.distance); } });return hits;
 }
 private static final class Hit { final Entity entity;final double x,y,z,distance;Hit(Entity e,double x,double y,double z,double d){entity=e;this.x=x;this.y=y;this.z=z;distance=d;} }
 private void applyAmmunition(EntityLivingBase victim) {
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ammunition);
  if(id.equals("minecraft:spectral_arrow") || id.equals("etfuturum:spectral_arrow")) { if(Content.uab) try { Object effect=Class.forName("ganymedes01.etfuturum.potion.ModPotions").getField("glowing").get(null);if(effect instanceof Potion)victim.addPotionEffect(new PotionEffect(((Potion)effect).id,200)); }catch(ReflectiveOperationException absent){} return; }
  if(!id.equals("minecraft:tipped_arrow") && !id.equals("etfuturum:tipped_arrow"))return;
  if(Content.uab && com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ammunition).equals("etfuturum:tipped_arrow")) {
   try { Object result=ammunition.getItem().getClass().getMethod("getEffects",ItemStack.class).invoke(ammunition.getItem(),ammunition);if(result instanceof Iterable)for(Object o:(Iterable<?>)result)if(o instanceof PotionEffect)victim.addPotionEffect(new PotionEffect((PotionEffect)o)); }catch(ReflectiveOperationException failure){throw new IllegalStateException("UAB tipped arrow adapter",failure);}return;
  }
  if(ammunition.hasTagCompound()) { NBTTagList list=ammunition.getTagCompound().getTagList("CustomPotionEffects",10);for(int i=0;i<list.tagCount();i++){PotionEffect effect=PotionEffect.readCustomPotionEffectFromNBT(list.getCompoundTagAt(i));if(effect!=null)victim.addPotionEffect(new PotionEffect(effect.getPotionID(),Math.max(1,effect.getDuration()/8),effect.getAmplifier()));}}
 }

 @Override public void writeEntityToNBT(NBTTagCompound tag) { super.writeEntityToNBT(tag);tag.setInteger("MCMPiercing",piercing);tag.setTag("MCMAmmo",ammunition.writeToNBT(new NBTTagCompound()));NBTTagList ids=new NBTTagList();for(UUID id:struck)ids.appendTag(new NBTTagString(id.toString()));tag.setTag("MCMPierced",ids); }
 @Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag);piercing=Math.max(0,Math.min(4,tag.getInteger("MCMPiercing")));ammunition=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MCMAmmo"));if(ammunition==null)ammunition=new ItemStack(Items.arrow);struck.clear();NBTTagList ids=tag.getTagList("MCMPierced",8);for(int i=0;i<Math.min(5,ids.tagCount());i++)try{struck.add(UUID.fromString(ids.getStringTagAt(i)));}catch(IllegalArgumentException invalid){} }
 public void writeSpawnData(ByteBuf b) { b.writeInt(shootingEntity==null?-1:shootingEntity.getEntityId());b.writeByte(piercing);ByteBufUtils.writeItemStack(b,ammunition); }
 public void readSpawnData(ByteBuf b) { shootingEntity=worldObj.getEntityByID(b.readInt());piercing=b.readUnsignedByte();ammunition=ByteBufUtils.readItemStack(b); }
}
