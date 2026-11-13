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
 private ItemStack ammunition=new ItemStack(Items.ARROW);
 private int piercing;
 private boolean nativeStep,nativeSuccess;
 private final Set<UUID> struck=new HashSet<UUID>();
 public CrossbowBolt(World world) { super(world); }
 public CrossbowBolt(World world,EntityPlayer shooter,ItemStack ammo,int piercing,boolean creative) {
  super(world,shooter);
  ammunition=ammo.copy();this.piercing=Math.max(0,Math.min(4,piercing));setDamage(2);setIsCritical(true);pickupStatus=creative?PickupStatus.CREATIVE_ONLY:PickupStatus.ALLOWED;
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
 public void record(Entity target,boolean success) { nativeSuccess=success; if(piercing>0)struck.add(target.getUniqueID());if(success && target instanceof EntityLivingBase && !world.isRemote) applyAmmunition((EntityLivingBase)target); }
 public void discard() { nativeStep=false;super.setDead(); }
 @Override public void setDead() { if(!(nativeStep && nativeSuccess && piercing>0))super.setDead(); }
 @Override public void onUpdate() {
  double x=posX,y=posY,z=posZ,vx=motionX,vy=motionY,vz=motionZ;
  List<Hit> remaining=piecingHits(x,y,z,vx,vy,vz);
  nativeSuccess=false;nativeStep=true;
  try { super.onUpdate(); } finally { nativeStep=false; }
  if(world.isRemote || isDead || piercing<=0 || !nativeSuccess) return;
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
  List<Hit> hits=new ArrayList<Hit>();if(piercing<=0 || world.isRemote)return hits;
  net.minecraft.util.math.Vec3d start=new net.minecraft.util.math.Vec3d(x,y,z),end=new net.minecraft.util.math.Vec3d(x+vx,y+vy,z+vz);
  net.minecraft.util.math.RayTraceResult block=world.rayTraceBlocks(start,end,false,true,false);if(block!=null)end=block.hitVec;
  for(Object object:world.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().expand(vx,vy,vz).grow(1,1,1))) {
   Entity entity=(Entity)object;if(!entity.canBeCollidedWith() || entity.isDead || entity==shootingEntity && ticksExisted<5 || struck.contains(entity.getUniqueID()))continue;
   net.minecraft.util.math.RayTraceResult hit=entity.getEntityBoundingBox().grow(.3,.3,.3).calculateIntercept(start,end);if(hit==null)continue;
   double hx=hit.hitVec.x,hy=hit.hitVec.y,hz=hit.hitVec.z;
   hits.add(new Hit(entity,hx,hy,hz,(hx-x)*(hx-x)+(hy-y)*(hy-y)+(hz-z)*(hz-z)));
  }
  Collections.sort(hits,new Comparator<Hit>() { public int compare(Hit a,Hit b) { return Double.compare(a.distance,b.distance); } });return hits;
 }
 private static final class Hit { final Entity entity;final double x,y,z,distance;Hit(Entity e,double x,double y,double z,double d){entity=e;this.x=x;this.y=y;this.z=z;distance=d;} }
 private void applyAmmunition(EntityLivingBase victim) {
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(ammunition);
  if(id.equals("minecraft:spectral_arrow") || id.equals("etfuturum:spectral_arrow")) { victim.addPotionEffect(new PotionEffect(net.minecraft.init.MobEffects.GLOWING,200)); return; }
  if(!id.equals("minecraft:tipped_arrow") && !id.equals("etfuturum:tipped_arrow"))return;
  for(PotionEffect effect:PotionUtils.getEffectsFromStack(ammunition))victim.addPotionEffect(new PotionEffect(effect.getPotion(),Math.max(1,effect.getDuration()/8),effect.getAmplifier(),effect.getIsAmbient(),effect.doesShowParticles()));
 }
 @Override protected ItemStack getArrowStack() { return ammunition.copy(); }
 @Override public void writeEntityToNBT(NBTTagCompound tag) { super.writeEntityToNBT(tag);tag.setInteger("MCMPiercing",piercing);tag.setTag("MCMAmmo",ammunition.writeToNBT(new NBTTagCompound()));NBTTagList ids=new NBTTagList();for(UUID id:struck)ids.appendTag(new NBTTagString(id.toString()));tag.setTag("MCMPierced",ids); }
 @Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag);piercing=Math.max(0,Math.min(4,tag.getInteger("MCMPiercing")));ammunition=new ItemStack(tag.getCompoundTag("MCMAmmo"));if(ammunition.isEmpty())ammunition=new ItemStack(Items.ARROW);struck.clear();NBTTagList ids=tag.getTagList("MCMPierced",8);for(int i=0;i<Math.min(5,ids.tagCount());i++)try{struck.add(UUID.fromString(ids.getStringTagAt(i)));}catch(IllegalArgumentException invalid){} }
 public void writeSpawnData(ByteBuf b) { b.writeInt(shootingEntity==null?-1:shootingEntity.getEntityId());b.writeByte(piercing);ByteBufUtils.writeItemStack(b,ammunition); }
 public void readSpawnData(ByteBuf b) { shootingEntity=world.getEntityByID(b.readInt());piercing=b.readUnsignedByte();ammunition=ByteBufUtils.readItemStack(b); }
}
