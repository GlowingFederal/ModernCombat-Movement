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
  super(level,source,x,y,z,radius,!wind&&radius==5,true);
  this.level=level;this.owner=owner;this.source=source;this.x=x;this.y=y;this.z=z;this.radius=radius;this.wind=wind;this.wet=wet;windMultiplier=multiplier;

  damage=new CombatBlastDamageSource(source,owner,x,y,z);
 }
 public static void anchor(World w,int x,int y,int z,boolean wet){run(new CombatBlast(w,null,null,x+.5,y+.5,z+.5,5,false,wet,1));}
 public static void crystal(World w,Entity source,Entity owner,double x,double y,double z){run(new CombatBlast(w,source,owner,x,y,z,6,false,false,1));}
 public static void charge(World w,Entity owner,double x,double y,double z){CombatBlast blast=new CombatBlast(w,null,owner,x,y,z,1.2F,true,false,1.22F);blast.windCharge=true;run(blast);}
 public static void wind(World w,Entity owner,double x,double y,double z,float radius,float multiplier){run(new CombatBlast(w,null,owner,x,y,z,radius,true,false,multiplier));}
 private static void run(CombatBlast blast){
  World w=blast.level;if(w.isRemote||net.minecraftforge.event.ForgeEventFactory.onExplosionStart(w,blast))return;
  blast.doExplosionA();
  if(blast.wind){blast.triggerBlocks();blast.getAffectedBlockPositions().clear();w.playSoundEffect(blast.x,blast.y,blast.z,"random.explode",.8F,1.5F);}
  else blast.doExplosionB(false);
  for(Object object:w.playerEntities){EntityPlayer p=(EntityPlayer)object;if(p instanceof EntityPlayerMP&&p.getDistanceSq(blast.x,blast.y,blast.z)<4096)
   ((EntityPlayerMP)p).playerNetServerHandler.sendPacket(new net.minecraft.network.play.server.S27PacketExplosion(blast.x,blast.y,blast.z,blast.radius,blast.getAffectedBlockPositions(),(Vec3)blast.getPlayerKnockbackMap().get(p)));
  }
 }
 @Override public void doExplosionA(){
  Set<BlockPos> blocks=new HashSet<BlockPos>();
  for(int ix=0;ix<16;ix++)for(int iy=0;iy<16;iy++)for(int iz=0;iz<16;iz++)if(ix==0||ix==15||iy==0||iy==15||iz==0||iz==15){
   double dx=ix/15F*2-1,dy=iy/15F*2-1,dz=iz/15F*2-1,length=Math.sqrt(dx*dx+dy*dy+dz*dz);dx/=length;dy/=length;dz/=length;
   double px=x,py=y,pz=z;
   for(float strength=radius*(.7F+level.rand.nextFloat()*.6F);strength>0;strength-=.22500001F){
    int bx=(int)Math.floor(px),by=(int)Math.floor(py),bz=(int)Math.floor(pz);if(by<0||by>=level.getHeight()||Math.abs((long)bx)>29999984L||Math.abs((long)bz)>29999984L)break;
    BlockPos position=new BlockPos(bx,by,bz);Block block=level.getBlockState(position).getBlock();
    if(wind){if(block==Blocks.bedrock||block==Blocks.barrier)strength-=(3600000+.3F)*.3F;}
    else if(wet&&bx==(int)Math.floor(x)&&by==(int)Math.floor(y)&&bz==(int)Math.floor(z))strength-=(100+.3F)*.3F;
    else if(block.getMaterial()!=Material.air)strength+=-(block.getExplosionResistance(level,position,null,this)+.3F)*.3F;
    if(strength>0)blocks.add(position);px+=dx*.3F;py+=dy*.3F;pz+=dz*.3F;
   }
  }
  getAffectedBlockPositions().addAll(blocks);
  double reach=radius*2;List<Entity> entities=level.getEntitiesWithinAABBExcludingEntity(source, new AxisAlignedBB(Math.floor(x-reach-1),Math.floor(y-reach-1),Math.floor(z-reach-1),Math.floor(x+reach+1),Math.floor(y+reach+1),Math.floor(z+reach+1)));
  net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(level,this,entities,reach);
  for(Entity entity:entities){
   if(entity.isImmuneToExplosions())continue;
   double distance=Math.sqrt(entity.getDistanceSq(x,y,z))/reach;if(distance>1)continue;
   double dx=entity.posX-x,dy=entity.posY+(entity instanceof EntityTNTPrimed?0:entity.getEyeHeight())-y,dz=entity.posZ-z,length=Math.sqrt(dx*dx+dy*dy+dz*dz);
   double exposure=level.getBlockDensity(new Vec3(x,y,z),entity.getEntityBoundingBox()),impact=(1-distance)*exposure;
   boolean shielded=!wind&&entity instanceof EntityPlayer&&com.glowingfederal.moderncombatmovement.compat.Shields.canBlock((EntityPlayer)entity,damage);
   if(!wind)entity.attackEntityFrom(damage,WeaponMath.explosionDamage(distance,exposure,radius));
   if(shielded)continue;
   double multiplier=wind?windMultiplier:1;
   if(wind&&entity instanceof EntityPlayer&&((EntityPlayer)entity).capabilities.isFlying)multiplier=0;
   double power=impact*multiplier*(Settings.knockback(false)?1-ArmorRules.explosionResistance(entity):1);
   Vec3 velocity=new Vec3(length==0?0:dx/length*power,length==0?0:dy/length*power,length==0?0:dz/length*power);
   if(windCharge&&entity instanceof EntityLivingBase)com.glowingfederal.moderncombatmovement.compat.WindCharges.launched((EntityLivingBase)entity);
   entity.addVelocity(velocity.xCoord,velocity.yCoord,velocity.zCoord);
   if(entity instanceof EntityPlayer&&!((EntityPlayer)entity).isSpectator()&&(!((EntityPlayer)entity).capabilities.isCreativeMode||!((EntityPlayer)entity).capabilities.isFlying))getPlayerKnockbackMap().put((EntityPlayer)entity,velocity);
  }
 }
 private void triggerBlocks(){
  Set<String> doors=new HashSet<String>();
  for(Object object:getAffectedBlockPositions()){BlockPos position=(BlockPos)object;int bx=position.getX(),by=position.getY(),bz=position.getZ();Block block=level.getBlockState(position).getBlock();
   Object key=net.minecraft.block.Block.blockRegistry.getNameForObject(block);if(key==null||!key.toString().startsWith("minecraft:"))continue;
   if(block==Blocks.fire){level.setBlockToAir(position);continue;}
   boolean door=block instanceof BlockDoor;if(door){int bottom=by;if(level.getBlockState(position).getValue(BlockDoor.HALF)==BlockDoor.EnumDoorHalf.UPPER)bottom--;if(!doors.add(bx+","+bottom+","+bz))continue;}
   boolean toggle=block instanceof BlockLever||block instanceof BlockButton||(block instanceof BlockFenceGate||block instanceof BlockDoor||block instanceof BlockTrapDoor)&&block.getMaterial()!=Material.iron;
   if(!toggle)continue;
   if(!(block instanceof BlockLever)&&level.isBlockPowered(position))continue;
   if(block instanceof BlockFenceGate&&!(owner instanceof EntityPlayer)){level.setBlockState(position,level.getBlockState(position).cycleProperty(BlockFenceGate.OPEN),3);continue;}
   block.onBlockActivated(level,position,level.getBlockState(position),owner instanceof EntityPlayer?(EntityPlayer)owner:null,EnumFacing.UP,.5F,.5F,.5F);
  }
 }
}
