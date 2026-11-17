package com.glowingfederal.moderncombatmovement.combat;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.init.Blocks;
import com.glowingfederal.moderncombatmovement.config.Settings;

/** ServerExplosion ray and entity calculations, confined to owned combat explosions.
 * Native phase B retains Forge's block destruction and drop hooks. Ordinary explosions are untouched. */
public final class CombatBlast extends Explosion {
 private final World level;
 private final double x,y,z;
 private final float radius,windMultiplier;
 private final boolean wind,wet;
 private boolean windCharge;
 private final Entity owner,source;
 private final CombatBlastDamageSource damage;
 private CombatBlast(World level,Entity source,Entity owner,double x,double y,double z,float radius,boolean wind,boolean wet,float multiplier){
  super(level,source,x,y,z,radius);
  this.level=level;this.owner=owner;this.source=source;this.x=x;this.y=y;this.z=z;this.radius=radius;this.wind=wind;this.wet=wet;windMultiplier=multiplier;
  isSmoking=true;isFlaming=!wind&&radius==5;
  damage=new CombatBlastDamageSource(source,owner,x,y,z);
 }
 public static void anchor(World w,int x,int y,int z,boolean wet){run(new CombatBlast(w,null,null,x+.5,y+.5,z+.5,5,false,wet,1));}
 public static void crystal(World w,Entity source,Entity owner,double x,double y,double z){run(new CombatBlast(w,source,owner,x,y,z,6,false,false,1));}
 public static void charge(World w,Entity owner,double x,double y,double z){CombatBlast blast=new CombatBlast(w,null,owner,x,y,z,1.2F,true,false,1.22F);blast.windCharge=true;run(blast);}
 public static void wind(World w,Entity owner,double x,double y,double z,float radius,float multiplier){run(new CombatBlast(w,null,owner,x,y,z,radius,true,false,multiplier));}
 public static void ritual(World w,Entity source,double x,double y,double z,float power,boolean terrain){CombatBlast blast=new CombatBlast(w,source,null,x,y,z,power,false,false,1);blast.isSmoking=terrain;blast.isFlaming=false;run(blast);}
 private static void run(CombatBlast blast){
  World w=blast.level;if(w.isRemote||net.minecraftforge.event.ForgeEventFactory.onExplosionStart(w,blast))return;
  blast.doExplosionA();
  if(blast.wind){blast.triggerBlocks();blast.affectedBlockPositions.clear();com.glowingfederal.moderncombatmovement.compat.ItemSounds.at(w,blast.x,blast.y,blast.z,blast.windCharge?"entity.wind_charge.wind_burst":"entity.breeze.wind_burst",1,1);}
  else blast.doExplosionB(false);
  if(!blast.isSmoking)blast.affectedBlockPositions.clear();
  for(Object object:w.playerEntities){EntityPlayer p=(EntityPlayer)object;if(p instanceof EntityPlayerMP&&p.getDistanceSq(blast.x,blast.y,blast.z)<4096)
   {if(blast.wind){Vec3 motion=(Vec3)blast.func_77277_b().get(p);com.glowingfederal.moderncombatmovement.network.Packets.channel.sendTo(new com.glowingfederal.moderncombatmovement.network.Packets.Feedback(p,0,blast.x,blast.y,blast.z,blast.radius,motion==null?0:motion.xCoord,motion==null?0:motion.yCoord,motion==null?0:motion.zCoord),(EntityPlayerMP)p);}else ((EntityPlayerMP)p).playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S27PacketExplosion(blast.x,blast.y,blast.z,blast.radius,blast.affectedBlockPositions,(Vec3)blast.func_77277_b().get(p))); }
  }
 }
 @Override public void doExplosionA(){
  Set<ChunkPosition> blocks=new HashSet<ChunkPosition>();
  for(int ix=0;ix<16;ix++)for(int iy=0;iy<16;iy++)for(int iz=0;iz<16;iz++)if(ix==0||ix==15||iy==0||iy==15||iz==0||iz==15){
   double dx=ix/15F*2-1,dy=iy/15F*2-1,dz=iz/15F*2-1,length=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=length;dy/=length;dz/=length;
   double px=x,py=y,pz=z;
   for(float strength=radius*(.7F+level.rand.nextFloat()*.6F);strength>0;strength-=.22500001F){
    int bx=(int)Math.floor(px),by=(int)Math.floor(py),bz=(int)Math.floor(pz);if(by<0||by>=level.getHeight()||Math.abs((long)bx)>29999984L||Math.abs((long)bz)>29999984L)break;
    ChunkPosition position=new ChunkPosition(bx,by,bz);Block block=level.getBlock(bx,by,bz);
    if(wind){if(block==Blocks.bedrock)strength-=(3600000+.3F)*.3F;}
    else if(wet&&bx==(int)Math.floor(x)&&by==(int)Math.floor(y)&&bz==(int)Math.floor(z))strength-=(100+.3F)*.3F;
    else if(block.getMaterial()!=Material.air)strength+=-(block.getExplosionResistance(null,level,bx,by,bz,x,y,z)+.3F)*.3F;
    if(strength>0)blocks.add(position);px+=dx*.3F;py+=dy*.3F;pz+=dz*.3F;
   }
  }
  affectedBlockPositions.addAll(blocks);
  double reach=radius*2;List<Entity> entities=level.getEntitiesWithinAABBExcludingEntity(source, AxisAlignedBB.getBoundingBox(Math.floor(x-reach-1),Math.floor(y-reach-1),Math.floor(z-reach-1),Math.floor(x+reach+1),Math.floor(y+reach+1),Math.floor(z+reach+1)));
  net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(level,this,entities,reach);
  for(Entity entity:entities){

   double distance=Math.sqrt(entity.getDistanceSq(x,y,z))/reach;if(distance>1)continue;
   double dx=entity.posX-x,dy=entity.posY+(entity instanceof EntityTNTPrimed?0:entity.getEyeHeight())-y,dz=entity.posZ-z,length=Math.sqrt(dx*dx+dy*dy+dz*dz);
   double exposure=level.getBlockDensity(Vec3.createVectorHelper(x,y,z),entity.boundingBox),impact=(1-distance)*exposure;
   boolean shielded=!wind&&entity instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.compat.Shields.canBlock((EntityPlayer)entity,damage);
   if(!wind)entity.attackEntityFrom(damage,WeaponMath.explosionDamage(distance,exposure,radius));
   if(shielded)continue;
   double multiplier=wind?windMultiplier:1;
   if(wind&&entity instanceof EntityPlayer&&((EntityPlayer)entity).capabilities.isFlying)multiplier=0;
   double power=impact*multiplier*(Settings.knockback(false)?1-ArmorRules.explosionResistance(entity):1);
   Vec3 velocity=Vec3.createVectorHelper(length==0?0:dx/length*power,length==0?0:dy/length*power,length==0?0:dz/length*power);
   if(windCharge&&entity instanceof EntityLivingBase)com.glowingfederal.moderncombatmovement.compat.WindCharges.launched((EntityLivingBase)entity);
   entity.addVelocity(velocity.xCoord,velocity.yCoord,velocity.zCoord);
   if(entity instanceof EntityPlayer&&(!((EntityPlayer)entity).capabilities.isCreativeMode||!((EntityPlayer)entity).capabilities.isFlying))func_77277_b().put((EntityPlayer)entity,velocity);
  }
 }
 private void triggerBlocks(){
  Set<String> doors=new HashSet<String>();
  for(Object object:affectedBlockPositions){ChunkPosition position=(ChunkPosition)object;int bx=position.chunkPosX,by=position.chunkPosY,bz=position.chunkPosZ;Block block=level.getBlock(bx,by,bz);
   if(com.glowingfederal.moderncombatmovement.compat.UabCombatBlocks.wind(level,bx,by,bz))continue;
   Object key=net.minecraft.block.Block.blockRegistry.getNameForObject(block);if(key==null||!key.toString().startsWith("minecraft:"))continue;
   if(block==Blocks.fire){level.setBlockToAir(bx,by,bz);continue;}
   boolean door=block instanceof BlockDoor;if(door){int bottom=by;if((level.getBlockMetadata(bx,by,bz)&8)!=0)bottom--;if(!doors.add(bx+","+bottom+","+bz))continue;}
   boolean toggle=block instanceof BlockLever||block instanceof BlockButton||(block instanceof BlockFenceGate||block instanceof BlockDoor||block instanceof BlockTrapDoor)&&block.getMaterial()!=Material.iron;
   if(!toggle)continue;
   if(!(block instanceof BlockLever)&&level.isBlockIndirectlyGettingPowered(bx,by,bz))continue;
   if(block instanceof BlockFenceGate&&!(owner instanceof EntityPlayer)){level.setBlockMetadataWithNotify(bx,by,bz,level.getBlockMetadata(bx,by,bz)^4,3);continue;}
   block.onBlockActivated(level,bx,by,bz,owner instanceof EntityPlayer?(EntityPlayer)owner:null,1,.5F,.5F,.5F);
  }
 }
}
