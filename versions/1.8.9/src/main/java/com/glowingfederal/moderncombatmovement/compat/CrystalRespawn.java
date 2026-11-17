/* Respawn stages, podium and spikes adapted from native 1.12 DragonSpawnManager,
 * DragonFightManager, WorldGenEndPodium and WorldGenSpikes. UAB supplies the legacy crystal/dragon lifecycle. */
package com.glowingfederal.moderncombatmovement.compat;
import java.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** One saved server owner for the legacy ritual. Native 1.12 retains its fight manager. */
public final class CrystalRespawn extends WorldSavedData {
 private static final String KEY="moderncombatmovement_end_fight";
 private int stage=-1,ticks,cx,cy,cz;
 private boolean hasPortal;
 private final List<UUID> ids=new ArrayList<UUID>();
 public CrystalRespawn(){this(KEY);}
 public CrystalRespawn(String name){super(name);}
 public static CrystalRespawn get(World w){
  CrystalRespawn data=(CrystalRespawn)w.getPerWorldStorage().loadData(CrystalRespawn.class,KEY);
  if(data==null){data=new CrystalRespawn(KEY);w.getPerWorldStorage().setData(KEY,data);}return data;
 }
 @SubscribeEvent public void world(TickEvent.WorldTickEvent e){if(e.phase==TickEvent.Phase.END&&!e.world.isRemote&&e.world.provider.getDimensionId()==1&&com.glowingfederal.moderncombatmovement.config.Settings.kit(false))get(e.world).tick(e.world);}
 public static void placed(EntityEnderCrystal crystal){
  World w=crystal.worldObj;if(w.isRemote||w.provider.getDimensionId()!=1||!com.glowingfederal.moderncombatmovement.config.Settings.kit(false))return;
  CrystalRespawn data=get(w);if(data.stage>=0)return;
  int bx=(int)Math.floor(crystal.posX),by=(int)Math.floor(crystal.posY)-1,bz=(int)Math.floor(crystal.posZ);
  if(!w.getEntitiesWithinAABB(EntityDragon.class,box(-256,0,-256,257,256,257)).isEmpty())return;
  // Both legacy and modern fountains have the bedrock center column; the old fountain can be away from zero.
  for(int x=bx-4;x<=bx+4;x++)for(int z=bz-4;z<=bz+4;z++){
   if(block(w,x,by,z)!=Blocks.bedrock||block(w,x,by+1,z)!=Blocks.bedrock||block(w,x,by+2,z)!=Blocks.bedrock)continue;
   int portals=0;for(int dx=-2;dx<=2;dx++)for(int dz=-2;dz<=2;dz++)if(block(w,x+dx,by,z+dz)==Blocks.end_portal)portals++;
   if(portals<16)continue;
   List<EntityEnderCrystal> crystals=new ArrayList<EntityEnderCrystal>();
   int[][] directions={{0,2},{0,-2},{2,0},{-2,0}};
   for(int[] d:directions){
    List<EntityEnderCrystal> found=w.getEntitiesWithinAABB(EntityEnderCrystal.class,box(x+d[0],by+1,z+d[1],x+d[0]+1,by+2,z+d[1]+1));
    for(EntityEnderCrystal e:found)if(!e.isDead&&Crystals.known(e)&&!crystals.contains(e)){crystals.add(e);break;}
   }
   if(crystals.size()!=4)continue;
   data.hasPortal=true;data.cx=x;data.cy=by;data.cz=z;data.ids.clear();for(EntityEnderCrystal e:crystals)data.ids.add(e.getUniqueID());
   data.stage=0;data.ticks=0;data.podium(w,false);data.markDirty();return;
  }
 }
 public static void destroyed(EntityEnderCrystal crystal){
  World w=crystal.worldObj;if(w.isRemote||w.provider.getDimensionId()!=1)return;
  CrystalRespawn data=get(w);if(data.stage>=0&&data.ids.contains(crystal.getUniqueID()))data.abort(w);
 }
 public void tick(World w){
  if(stage<0)return;
  // Reload the small fountain area before resolving saved UUIDs; absence after load aborts the ritual.
  for(int dx:new int[]{-6,6})for(int dz:new int[]{-6,6})w.getChunkFromBlockCoords(new net.minecraft.util.BlockPos(cx+dx,cy,cz+dz));
  List<EntityEnderCrystal> crystals=new ArrayList<EntityEnderCrystal>();
  for(Object object:w.getEntitiesWithinAABB(EntityEnderCrystal.class,box(cx-6,cy,cz-6,cx+7,cy+5,cz+7))){EntityEnderCrystal e=(EntityEnderCrystal)object;if(!e.isDead&&ids.contains(e.getUniqueID()))crystals.add(e);}
  if(crystals.size()!=4){abort(w);return;}
  if(stage==0){beam(crystals,0,128,0,true);next(1);}
  else if(stage==1){if(ticks<100){if(ticks==0||ticks==50||ticks==51||ticks==52||ticks>=95)event(w);}else next(2);}
  else if(stage==2){
   int index=ticks/40;
   if(index<10){int[] spike=spike(w,index);if(ticks%40==0)beam(crystals,spike[0],spike[2]+1,spike[1],true);else if(ticks%40==39)regenerate(w,spike);}
   else if(ticks%40==0)next(3);
  }else if(stage==3){
   if(ticks>=100){
    EntityDragon dragon;
    if(Content.uab)try{dragon=(EntityDragon)Class.forName("ganymedes01.etfuturum.entities.EntityRespawnedDragon").getConstructor(World.class).newInstance(w);}catch(ReflectiveOperationException failure){throw new IllegalStateException("UAB respawned dragon changed",failure);}
    else dragon=new RespawnedDragon(w);
    dragon.setLocationAndAngles(0,128,0,w.rand.nextFloat()*360,0);
    if(!w.spawnEntityInWorld(dragon))return;
    stage=-1;ids.clear();ticks=0;resetSpikes(w);
    for(EntityEnderCrystal e:crystals){beam(e,0,0,0,false);e.setDead();}
    for(EntityEnderCrystal e:crystals)com.glowingfederal.moderncombatmovement.combat.CombatBlast.ritual(w,e,e.posX,e.posY,e.posZ,6,false);
    markDirty();return;
   }else if(ticks>=80||ticks>0&&ticks<5)event(w);else if(ticks==0)beam(crystals,0,128,0,true);
  }
  ticks++;markDirty();
 }
 public static void dragonDefeated(EntityDragon dragon){
  World w=dragon.worldObj;CrystalRespawn data=(CrystalRespawn)w.getPerWorldStorage().loadData(CrystalRespawn.class,KEY);if(data!=null&&data.hasPortal)data.podium(w,true);
 }
 private void next(int next){stage=next;ticks=-1;}
 private void abort(World w){
  stage=-1;ids.clear();ticks=0;
  for(Object object:w.getEntitiesWithinAABB(EntityEnderCrystal.class,box(cx-6,cy,cz-6,cx+7,cy+5,cz+7)))beam((EntityEnderCrystal)object,0,0,0,false);
  resetSpikes(w);podium(w,true);markDirty();
 }
 private static void beam(List<EntityEnderCrystal> crystals,int x,int y,int z,boolean active){for(EntityEnderCrystal e:crystals)beam(e,x,y,z,active);}
 private static void beam(EntityEnderCrystal crystal,int x,int y,int z,boolean active){if(Crystals.known(crystal))((CrystalState)crystal).mcm$beam(x,y,z,active);}
 private static int[] spike(World w,int index){
  List<Integer> order=new ArrayList<Integer>();for(int i=0;i<10;i++)order.add(i);Collections.shuffle(order,new Random(new Random(w.getSeed()).nextLong()&65535L));
  int value=order.get(index);return new int[]{(int)(42*Math.cos(2*(-Math.PI+Math.PI/10*index))),(int)(42*Math.sin(2*(-Math.PI+Math.PI/10*index))),76+value*3,2+value/3,value==1||value==2?1:0};
 }
 private void regenerate(World w,int[] s){
  int x=s[0],z=s[1],height=s[2],radius=s[3];
  for(Object object:w.getEntitiesWithinAABB(EntityEnderCrystal.class,box(x-10,height-10,z-10,x+11,height+11,z+11)))if(Crystals.known((EntityEnderCrystal)object))((EntityEnderCrystal)object).setDead();
  for(int dx=-10;dx<=10;dx++)for(int dy=height-10;dy<=height+10;dy++)for(int dz=-10;dz<=10;dz++)set(w,x+dx,dy,z+dz,Blocks.air);
  com.glowingfederal.moderncombatmovement.combat.CombatBlast.ritual(w,null,x+.5,height,z+.5,5,true);
  for(int dx=-radius;dx<=radius;dx++)for(int y=0;y<=height+10;y++)for(int dz=-radius;dz<=radius;dz++){
   if(dx*dx+dz*dz<=radius*radius+1&&y<height)set(w,x+dx,y,z+dz,Blocks.obsidian);else if(y>65)set(w,x+dx,y,z+dz,Blocks.air);
  }
  if(s[4]!=0)for(int dx=-2;dx<=2;dx++)for(int dz=-2;dz<=2;dz++){
   if(Math.abs(dx)==2||Math.abs(dz)==2)for(int y=height;y<=height+2;y++)set(w,x+dx,y,z+dz,Blocks.iron_bars);
   set(w,x+dx,height+3,z+dz,Blocks.iron_bars);
  }
  EntityEnderCrystal crystal=new EntityEnderCrystal(w);crystal.setLocationAndAngles(x+.5,height+1,z+.5,w.rand.nextFloat()*360,0);((CrystalState)crystal).mcm$invulnerable(true);beam(crystal,0,128,0,true);w.spawnEntityInWorld(crystal);set(w,x,height,z,Blocks.bedrock);
 }
 private static void resetSpikes(World w){for(int i=0;i<10;i++){int[] s=spike(w,i);for(Object object:w.getEntitiesWithinAABB(EntityEnderCrystal.class,box(s[0]-s[3],0,s[1]-s[3],s[0]+s[3]+1,256,s[1]+s[3]+1))){EntityEnderCrystal e=(EntityEnderCrystal)object;if(Crystals.known(e)){((CrystalState)e).mcm$invulnerable(false);beam(e,0,0,0,false);}}}}
 private void podium(World w,boolean active){
  // Preserve the native legacy fountain location while applying the native modern open/closed lifecycle.
  for(int dx=-4;dx<=4;dx++)for(int y=cy-1;y<=cy+32;y++)for(int dz=-4;dz<=4;dz++){
   double distance=Math.sqrt(dx*dx+dz*dz);if(distance>3.5)continue;
   if(y<cy)set(w,cx+dx,y,cz+dz,distance<=2.5?Blocks.bedrock:Blocks.end_stone);
   else if(y>cy)set(w,cx+dx,y,cz+dz,Blocks.air);
   else set(w,cx+dx,y,cz+dz,distance>2.5?Blocks.bedrock:active?Blocks.end_portal:Blocks.air);
  }
  for(int y=0;y<4;y++)set(w,cx,cy+y,cz,Blocks.bedrock);
  set(w,cx-1,cy+2,cz,Blocks.torch);set(w,cx+1,cy+2,cz,Blocks.torch);set(w,cx,cy+2,cz-1,Blocks.torch);set(w,cx,cy+2,cz+1,Blocks.torch);
 }
 private static void event(World w){ItemSounds.at(w,0,128,0,"entity.ender_dragon.growl",64,.8F+w.rand.nextFloat()*.3F);}
 private static Block block(World w,int x,int y,int z){return w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)).getBlock();}
 private static void set(World w,int x,int y,int z,Block block){w.setBlockState(new net.minecraft.util.BlockPos(x,y,z),block.getDefaultState(),2);}
 private static net.minecraft.util.AxisAlignedBB box(double x,double y,double z,double x1,double y1,double z1){return new net.minecraft.util.AxisAlignedBB(x,y,z,x1,y1,z1);}
 @Override public void readFromNBT(NBTTagCompound tag){hasPortal=tag.getBoolean("HasPortal");stage=tag.hasKey("Stage")?tag.getInteger("Stage"):-1;if(stage<0||stage>3)stage=-1;ticks=Math.max(0,tag.getInteger("Ticks"));cx=tag.getInteger("X");cy=tag.getInteger("Y");cz=tag.getInteger("Z");ids.clear();for(int i=0;i<4;i++)try{ids.add(UUID.fromString(tag.getString("Crystal"+i)));}catch(IllegalArgumentException ignored){}}
 @Override public void writeToNBT(NBTTagCompound tag){tag.setBoolean("HasPortal",hasPortal);tag.setInteger("Stage",stage);tag.setInteger("Ticks",ticks);tag.setInteger("X",cx);tag.setInteger("Y",cy);tag.setInteger("Z",cz);for(int i=0;i<ids.size();i++)tag.setString("Crystal"+i,ids.get(i).toString());}
}
