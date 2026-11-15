package com.glowingfederal.moderncombatmovement.compat;

import java.util.UUID;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import io.netty.buffer.ByteBuf;
import com.glowingfederal.moderncombatmovement.movement.*;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
/** Attached vanilla firework lifecycle; unrelated fireworks and projectiles are never intercepted. */
public final class BoostRocket extends Entity implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
 private boolean shotAtAngle;
 private int life,lifetime=20;private EntityPlayer owner;private UUID ownerId;
 public static BoostRocket crossbow(World world,EntityPlayer player,ItemStack stack,double[] direction){BoostRocket rocket=new BoostRocket(world,player,stack);rocket.shotAtAngle=true;rocket.dataWatcher.updateObject(9,Integer.valueOf(-1));rocket.setPosition(player.posX,player.getEntityBoundingBox().minY+player.getEyeHeight()-.15,player.posZ);rocket.motionX=direction[0]*1.6;rocket.motionY=direction[1]*1.6;rocket.motionZ=direction[2]*1.6;return rocket;}
 public BoostRocket(World w){super(w);setSize(.25F,.25F);}
 public BoostRocket(World w,EntityPlayer p,ItemStack stack){this(w);owner=p;ownerId=p.getUniqueID();ItemStack copy=stack.copy();copy.stackSize=1;dataWatcher.updateObject(8,copy);dataWatcher.updateObject(9,Integer.valueOf(p.getEntityId()));int flight=stack.hasTagCompound()&&stack.getTagCompound().hasKey("Fireworks",10)?stack.getTagCompound().getCompoundTag("Fireworks").getByte("Flight")&255:1;lifetime=10*(1+flight)+rand.nextInt(6)+rand.nextInt(7);setPosition(p.posX,p.posY,p.posZ);}
 @Override protected void entityInit(){dataWatcher.addObject(8,new ItemStack(Items.fireworks));dataWatcher.addObject(9,Integer.valueOf(-1));}
 public ItemStack item(){return dataWatcher.getWatchableObjectItemStack(8);}
 private NBTTagCompound fireworks(){ItemStack stack=item();return stack!=null&&stack.hasTagCompound()?stack.getTagCompound().getCompoundTag("Fireworks"):new NBTTagCompound();}
 @Override public void onUpdate(){
  super.onUpdate();prevPosX=posX;prevPosY=posY;prevPosZ=posZ;
  if(owner==null){Entity e=worldObj.getEntityByID(dataWatcher.getWatchableObjectInt(9));if(e instanceof EntityPlayer)owner=(EntityPlayer)e;else if(!worldObj.isRemote&&ownerId!=null)owner=worldObj.getPlayerEntityByUUID(ownerId);}
  if(dataWatcher.getWatchableObjectInt(9)>=0&&owner!=null&&owner.isEntityAlive()&&owner.worldObj==worldObj){
   double offsetX=0,offsetZ=0;
   if(FlightHooks.flying(owner)){Vec3 look=FlightHooks.look(owner);FlightMotion speed=PlayerHooks.state(owner).flightMotion;speed.set(owner.motionX,owner.motionY,owner.motionZ);FlightMath.boost(speed,look.xCoord,look.yCoord,look.zCoord);owner.motionX=speed.x;owner.motionY=speed.y;owner.motionZ=speed.z;float angle=(owner.rotationYaw+80)*(float)(Math.PI/180);offsetX=-MathHelper.sin(angle)*.5;offsetZ=MathHelper.cos(angle)*.5;}
   setPosition(owner.posX+offsetX,owner.getEntityBoundingBox().minY,owner.posZ+offsetZ);motionX=owner.motionX;motionY=owner.motionY;motionZ=owner.motionZ;
  }else{if(!shotAtAngle){motionX*= isCollidedHorizontally?1:1.15;motionZ*=isCollidedHorizontally?1:1.15;motionY+=.04;}moveEntity(motionX,motionY,motionZ);}
  if(life==0)worldObj.playSoundAtEntity(this,"fireworks.launch",3,1);life++;
  if(worldObj.isRemote)worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,posX,posY,posZ,rand.nextGaussian()*.05,-motionY*.5,rand.nextGaussian()*.05);
  if(!worldObj.isRemote){boolean stars=fireworks().getTagList("Explosions",10).tagCount()>0;MovingObjectPosition hit=worldObj.rayTraceBlocks(new Vec3(posX,posY,posZ),new Vec3(posX+motionX,posY+motionY,posZ+motionZ),false,true,false);if(life>lifetime||hitsEntity(hit)||stars&&hit!=null)explode();}
 }

 private boolean hitsEntity(MovingObjectPosition obstruction){
  double ex=posX+motionX,ey=posY+motionY,ez=posZ+motionZ;if(obstruction!=null){ex=obstruction.hitVec.xCoord;ey=obstruction.hitVec.yCoord;ez=obstruction.hitVec.zCoord;}
  for(Object object:worldObj.getEntitiesWithinAABBExcludingEntity(this,getEntityBoundingBox().addCoord(motionX,motionY,motionZ).expand(1,1,1))){Entity target=(Entity)object;if(target==owner||target.isDead||!target.canBeCollidedWith())continue;
   AxisAlignedBB b=target.getEntityBoundingBox().expand(.3,.3,.3);if(b.isVecInside(new Vec3(posX,posY,posZ))||b.calculateIntercept(new Vec3(posX,posY,posZ),new Vec3(ex,ey,ez))!=null)return true;
  }return false;
 }
 private void explode(){
  worldObj.setEntityState(this,(byte)17);int stars=fireworks().getTagList("Explosions",10).tagCount();
  if(stars>0){float damage=5+stars*2;BoostRocketDamage source=new BoostRocketDamage(this,owner);if(dataWatcher.getWatchableObjectInt(9)>=0&&owner!=null)owner.attackEntityFrom(source,damage);
   for(Object object:worldObj.getEntitiesWithinAABB(EntityLivingBase.class,getEntityBoundingBox().expand(5,5,5))){EntityLivingBase target=(EntityLivingBase)object;double distance=getDistanceToEntity(target);if(dataWatcher.getWatchableObjectInt(9)>=0&&target==owner||distance>5)continue;boolean visible=false;for(int i=0;i<2;i++)if(worldObj.rayTraceBlocks(new Vec3(posX,posY,posZ),new Vec3(target.posX,target.getEntityBoundingBox().minY+target.height*.5*i,target.posZ),false,true,false)==null){visible=true;break;}if(visible)target.attackEntityFrom(source,damage*(float)Math.sqrt((5-distance)/5));}
  }setDead();
 }
 @Override public void handleStatusUpdate(byte state){if(state==17)ModernCombatMovement.proxy.fireworks(worldObj,posX,posY,posZ,motionX,motionY,motionZ,fireworks());else super.handleStatusUpdate(state);}
 @Override protected void writeEntityToNBT(NBTTagCompound tag){tag.setBoolean("ShotAtAngle",shotAtAngle);tag.setInteger("Life",life);tag.setInteger("LifeTime",lifetime);if(ownerId!=null)tag.setString("Owner",ownerId.toString());NBTTagCompound stack=new NBTTagCompound();item().writeToNBT(stack);tag.setTag("FireworksItem",stack);}
 @Override protected void readEntityFromNBT(NBTTagCompound tag){shotAtAngle=tag.getBoolean("ShotAtAngle");life=tag.getInteger("Life");lifetime=tag.getInteger("LifeTime");ItemStack stack=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FireworksItem"));if(stack!=null)dataWatcher.updateObject(8,stack);if(tag.hasKey("Owner"))try{ownerId=UUID.fromString(tag.getString("Owner"));}catch(IllegalArgumentException invalid){ownerId=null;}}
 public void writeSpawnData(ByteBuf b){b.writeBoolean(shotAtAngle);b.writeInt(life);b.writeInt(lifetime);}
 public void readSpawnData(ByteBuf b){shotAtAngle=b.readBoolean();life=b.readInt();lifetime=b.readInt();}
}
