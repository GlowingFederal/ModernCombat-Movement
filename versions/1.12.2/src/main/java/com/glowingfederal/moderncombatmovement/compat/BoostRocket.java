package com.glowingfederal.moderncombatmovement.compat;

import java.util.UUID;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.network.datasync.*;
import io.netty.buffer.ByteBuf;
import com.glowingfederal.moderncombatmovement.movement.*;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
/** Attached vanilla firework lifecycle; unrelated fireworks and projectiles are never intercepted. */
public final class BoostRocket extends Entity implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
 private static final DataParameter<ItemStack> ITEM=EntityDataManager.createKey(BoostRocket.class,DataSerializers.ITEM_STACK);
 private static final DataParameter<Integer> OWNER=EntityDataManager.createKey(BoostRocket.class,DataSerializers.VARINT);
 private boolean shotAtAngle;
 private int life,lifetime=20;private EntityPlayer owner;private UUID ownerId;
 public static BoostRocket crossbow(World world,EntityPlayer player,ItemStack stack,double[] direction){BoostRocket rocket=new BoostRocket(world,player,stack);rocket.shotAtAngle=true;rocket.dataManager.set(OWNER,-1);rocket.setPosition(player.posX,player.getEntityBoundingBox().minY+player.getEyeHeight()-.15,player.posZ);rocket.motionX=direction[0]*1.6;rocket.motionY=direction[1]*1.6;rocket.motionZ=direction[2]*1.6;return rocket;}
 public BoostRocket(World w){super(w);setSize(.25F,.25F);}
 public BoostRocket(World w,EntityPlayer p,ItemStack stack){this(w);owner=p;ownerId=p.getUniqueID();ItemStack copy=stack.copy();copy.setCount(1);dataManager.set(ITEM,copy);dataManager.set(OWNER,p.getEntityId());int flight=stack.hasTagCompound()&&stack.getTagCompound().hasKey("Fireworks",10)?stack.getTagCompound().getCompoundTag("Fireworks").getByte("Flight")&255:1;lifetime=10*(1+flight)+rand.nextInt(6)+rand.nextInt(7);setPosition(p.posX,p.posY,p.posZ);}
 @Override protected void entityInit(){dataManager.register(ITEM,new ItemStack(Items.FIREWORKS));dataManager.register(OWNER,-1);}
 public ItemStack item(){return dataManager.get(ITEM);}
 private NBTTagCompound fireworks(){ItemStack stack=item();return stack!=null&&stack.hasTagCompound()?stack.getTagCompound().getCompoundTag("Fireworks"):new NBTTagCompound();}
 @Override public void onUpdate(){
  super.onUpdate();prevPosX=posX;prevPosY=posY;prevPosZ=posZ;
  if(owner==null){Entity e=world.getEntityByID(dataManager.get(OWNER));if(e instanceof EntityPlayer)owner=(EntityPlayer)e;else if(!world.isRemote&&ownerId!=null)owner=world.getPlayerEntityByUUID(ownerId);}
  if(dataManager.get(OWNER)>=0&&owner!=null&&owner.isEntityAlive()&&owner.world==world){
   double offsetX=0,offsetZ=0;
   if(FlightHooks.flying(owner)){Vec3d look=FlightHooks.look(owner);FlightMotion speed=PlayerHooks.state(owner).flightMotion;speed.set(owner.motionX,owner.motionY,owner.motionZ);FlightMath.boost(speed,look.x,look.y,look.z);owner.motionX=speed.x;owner.motionY=speed.y;owner.motionZ=speed.z;float angle=(owner.rotationYaw+80)*(float)(Math.PI/180);offsetX=-MathHelper.sin(angle)*.5;offsetZ=MathHelper.cos(angle)*.5;}
   setPosition(owner.posX+offsetX,owner.getEntityBoundingBox().minY,owner.posZ+offsetZ);motionX=owner.motionX;motionY=owner.motionY;motionZ=owner.motionZ;
  }else{if(!shotAtAngle){motionX*= collidedHorizontally?1:1.15;motionZ*=collidedHorizontally?1:1.15;motionY+=.04;}move(MoverType.SELF,motionX,motionY,motionZ);}
  if(life==0)world.playSound(null,posX,posY,posZ,net.minecraft.init.SoundEvents.ENTITY_FIREWORK_LAUNCH,SoundCategory.AMBIENT,3,1);life++;
  if(world.isRemote)world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,posX,posY,posZ,rand.nextGaussian()*.05,-motionY*.5,rand.nextGaussian()*.05);
  if(!world.isRemote){boolean stars=fireworks().getTagList("Explosions",10).tagCount()>0;RayTraceResult hit=world.rayTraceBlocks(new Vec3d(posX,posY,posZ),new Vec3d(posX+motionX,posY+motionY,posZ+motionZ),false,true,false);if(life>lifetime||hitsEntity(hit)||stars&&hit!=null)explode();}
 }

 private boolean hitsEntity(RayTraceResult obstruction){
  double ex=posX+motionX,ey=posY+motionY,ez=posZ+motionZ;if(obstruction!=null){ex=obstruction.hitVec.x;ey=obstruction.hitVec.y;ez=obstruction.hitVec.z;}
  for(Object object:world.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().expand(motionX,motionY,motionZ).grow(1,1,1))){Entity target=(Entity)object;if(target==owner||target.isDead||!target.canBeCollidedWith())continue;
   AxisAlignedBB b=target.getEntityBoundingBox().grow(.3,.3,.3);if(b.contains(new Vec3d(posX,posY,posZ))||b.calculateIntercept(new Vec3d(posX,posY,posZ),new Vec3d(ex,ey,ez))!=null)return true;
  }return false;
 }
 private void explode(){
  world.setEntityState(this,(byte)17);int stars=fireworks().getTagList("Explosions",10).tagCount();
  if(stars>0){float damage=5+stars*2;BoostRocketDamage source=new BoostRocketDamage(this,owner);if(dataManager.get(OWNER)>=0&&owner!=null)owner.attackEntityFrom(source,damage);
   for(Object object:world.getEntitiesWithinAABB(EntityLivingBase.class,getEntityBoundingBox().grow(5,5,5))){EntityLivingBase target=(EntityLivingBase)object;double distance=getDistance(target);if(dataManager.get(OWNER)>=0&&target==owner||distance>5)continue;boolean visible=false;for(int i=0;i<2;i++)if(world.rayTraceBlocks(new Vec3d(posX,posY,posZ),new Vec3d(target.posX,target.getEntityBoundingBox().minY+target.height*.5*i,target.posZ),false,true,false)==null){visible=true;break;}if(visible)target.attackEntityFrom(source,damage*(float)Math.sqrt((5-distance)/5));}
  }setDead();
 }
 @Override public void handleStatusUpdate(byte state){if(state==17)ModernCombatMovement.proxy.fireworks(world,posX,posY,posZ,motionX,motionY,motionZ,fireworks());else super.handleStatusUpdate(state);}
 @Override protected void writeEntityToNBT(NBTTagCompound tag){tag.setBoolean("ShotAtAngle",shotAtAngle);tag.setInteger("Life",life);tag.setInteger("LifeTime",lifetime);if(ownerId!=null)tag.setString("Owner",ownerId.toString());NBTTagCompound stack=new NBTTagCompound();item().writeToNBT(stack);tag.setTag("FireworksItem",stack);}
 @Override protected void readEntityFromNBT(NBTTagCompound tag){shotAtAngle=tag.getBoolean("ShotAtAngle");life=tag.getInteger("Life");lifetime=tag.getInteger("LifeTime");ItemStack stack=new ItemStack(tag.getCompoundTag("FireworksItem"));if(stack!=null)dataManager.set(ITEM,stack);if(tag.hasKey("Owner"))try{ownerId=UUID.fromString(tag.getString("Owner"));}catch(IllegalArgumentException invalid){ownerId=null;}}
 public void writeSpawnData(ByteBuf b){b.writeBoolean(shotAtAngle);b.writeInt(life);b.writeInt(lifetime);}
 public void readSpawnData(ByteBuf b){shotAtAngle=b.readBoolean();life=b.readInt();lifetime=b.readInt();}
}
