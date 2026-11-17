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
 private boolean crossbow;
 private boolean nativeStep,nativeSuccess;
 private final Set<UUID> struck=new HashSet<UUID>();
 public CrossbowBolt(World world) { super(world); }
 public CrossbowBolt(World world,EntityPlayer shooter,ItemStack ammo,int piercing,boolean creative) {
  super(world,shooter,1F);
  crossbow=true;ammunition=ammo.copy();this.piercing=Math.max(0,Math.min(4,piercing));setDamage(2);setIsCritical(true);canBePickedUp=creative?2:1;
 }
 public CrossbowBolt(World world,double x,double y,double z,ItemStack ammo){super(world,x,y,z);ammunition=ammo.copy();ammunition.stackSize=1;canBePickedUp=1;}
 public CrossbowBolt(World world,EntityPlayer shooter,ItemStack ammo,float charge,boolean creative){
  super(world,shooter,charge*2);ammunition=ammo.copy();ammunition.stackSize=1;canBePickedUp=creative?2:1;setIsCritical(charge==1);
  setPosition(shooter.posX,shooter.posY+shooter.getEyeHeight()-.1,shooter.posZ);
  double[] look=com.glowingfederal.moderncombatmovement.combat.CrossbowMath.direction(shooter.rotationYaw,shooter.rotationPitch,0);launchSpeed(look[0],look[1],look[2],charge*3);
  motionX+=shooter.motionX;motionZ+=shooter.motionZ;if(!shooter.onGround)motionY+=shooter.motionY;
 }
 /** UAB EntityTippedArrow pickup preserves the actual saved ammunition. */
 @Override public void onCollideWithPlayer(EntityPlayer player){
  if(!worldObj.isRemote&&((ArrowState)(Object)this).mcm$inGround()&&arrowShake<=0){
   boolean allowed=canBePickedUp==1||canBePickedUp==2&&player.capabilities.isCreativeMode;
   ItemStack stack=ammunition.copy();stack.stackSize=1;
   if(canBePickedUp==1&&!player.inventory.addItemStackToInventory(stack))allowed=false;
   if(allowed){playSound("random.pop",.2F,((rand.nextFloat()-rand.nextFloat())*.7F+1)*2);player.onItemPickup(this,1);setDead();}
  }
 }
 public boolean spectral(){return ammunition!=null&&ammunition.getItem()==Ammunition.spectral;}
 public int piercing() { return piercing; }
 public void launch(double x,double y,double z) {launchSpeed(x,y,z,3.15F);}
 private void launchSpeed(double x,double y,double z,float speed) {
  double length=Math.sqrt(x*x+y*y+z*z);
  motionX=(x/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*speed;
  motionY=(y/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*speed;
  motionZ=(z/length+(rand.nextDouble()-rand.nextDouble())*.0172275)*speed;
  rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);
  rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
 }
 public boolean mayHit(Entity target) { return piercing<=0 || !struck.contains(target.getUniqueID()) && struck.size()<piercing+1; }
 public List<Entity> filter(List<Entity> entities) { if(piercing<=0)return entities;List<Entity> result=new ArrayList<Entity>();for(Entity e:entities)if(!struck.contains(e.getUniqueID()))result.add(e);return result; }
 public void record(Entity target,boolean success) { nativeSuccess=success; if(piercing>0)struck.add(target.getUniqueID());if(success && target instanceof EntityLivingBase && !worldObj.isRemote) applyAmmunition((EntityLivingBase)target); }
 public void discard() { nativeStep=false;super.setDead(); }
 @Override public void setDead() { if(!(nativeStep && nativeSuccess && piercing>0))super.setDead(); }
 @Override public void onUpdate() {
  if(worldObj.isRemote){
   boolean grounded=((ArrowState)(Object)this).mcm$inGround();int count=grounded?(ticksExisted%5==0?1:0):2;
   if(count>0&&(spectral()||ammunition.getItem()==Ammunition.tipped)){
    int color=spectral()?9740385:net.minecraft.potion.PotionHelper.calcPotionLiquidColor(Ammunition.effects(ammunition));
    double red=(color>>16&255)/255D,green=(color>>8&255)/255D,blue=(color&255)/255D;
    for(int i=0;i<count;i++)worldObj.spawnParticle(net.minecraft.util.EnumParticleTypes.SPELL_MOB,posX+(rand.nextDouble()-.5)*width,posY+rand.nextDouble()*height,posZ+(rand.nextDouble()-.5)*width,red,green,blue);
   }
  }
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
  if(id.equals("moderncombatmovement:spectral_arrow")||id.equals("etfuturum:spectral_arrow")){if(Ammunition.glowing!=null)victim.addPotionEffect(new PotionEffect(Ammunition.glowing.id,200));return;}
  if(id.equals("moderncombatmovement:tipped_arrow")||id.equals("etfuturum:tipped_arrow"))for(PotionEffect effect:Ammunition.effects(ammunition))victim.addPotionEffect(new PotionEffect(effect));
 }

 @Override public void writeEntityToNBT(NBTTagCompound tag) { super.writeEntityToNBT(tag);tag.setBoolean("MCMCrossbow",crossbow);tag.setInteger("MCMPiercing",piercing);tag.setTag("MCMAmmo",ammunition.writeToNBT(new NBTTagCompound()));NBTTagList ids=new NBTTagList();for(UUID id:struck)ids.appendTag(new NBTTagString(id.toString()));tag.setTag("MCMPierced",ids); }
 @Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag);piercing=Math.max(0,Math.min(4,tag.getInteger("MCMPiercing")));crossbow=tag.hasKey("MCMCrossbow")?tag.getBoolean("MCMCrossbow"):piercing>0;ammunition=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MCMAmmo"));if(ammunition==null)ammunition=new ItemStack(Items.arrow);struck.clear();NBTTagList ids=tag.getTagList("MCMPierced",8);for(int i=0;i<Math.min(5,ids.tagCount());i++)try{struck.add(UUID.fromString(ids.getStringTagAt(i)));}catch(IllegalArgumentException invalid){} }
 public void writeSpawnData(ByteBuf b) { b.writeInt(shootingEntity==null?-1:shootingEntity.getEntityId());b.writeBoolean(crossbow);b.writeByte(piercing);ByteBufUtils.writeItemStack(b,ammunition); }
 public void readSpawnData(ByteBuf b) { shootingEntity=worldObj.getEntityByID(b.readInt());crossbow=b.readBoolean();piercing=b.readUnsignedByte();ammunition=ByteBufUtils.readItemStack(b); }
 @Override public void playSound(String sound,float volume,float pitch){if(crossbow&&sound.equals("random.bowhit"))ItemSounds.play(this,"item.crossbow.hit",volume,pitch);else super.playSound(sound,volume,pitch);}

}
