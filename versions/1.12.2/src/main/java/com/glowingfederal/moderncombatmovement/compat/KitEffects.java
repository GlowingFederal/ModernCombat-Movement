package com.glowingfederal.moderncombatmovement.compat;

import java.io.File;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.*;
import net.minecraft.potion.*;
import net.minecraft.nbt.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.living.*;
public final class KitEffects {
 public static final String[] NAMES={"weaving","oozing","infested","wind_charged","slow_falling"};
 public static final Potion[] EFFECTS=new Potion[5];
 private static final int[] COLORS={7891290,10092451,9214860,12438015,15978425};
 private KitEffects() {}
 public static void register(File file) {
  for(int i=0;i<NAMES.length;i++){
   Potion existing=Potion.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("etfuturum:"+NAMES[i]));
   if(existing!=null)EFFECTS[i]=existing;
   else{EFFECTS[i]=new Effect(NAMES[i],COLORS[i]).setRegistryName("moderncombatmovement",NAMES[i]);net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Potion.class).register(EFFECTS[i]);}
   type(NAMES[i],NAMES[i],new PotionEffect(EFFECTS[i],i==4?1800:3600));
  }
  type("long_slow_falling","slow_falling",new PotionEffect(EFFECTS[4],4800));
  type("strong_slowness","slowness",new PotionEffect(MobEffects.SLOWNESS,400,3));
  type("turtle_master","turtle_master",new PotionEffect(MobEffects.SLOWNESS,400,3),new PotionEffect(MobEffects.RESISTANCE,400,2));
  type("long_turtle_master","turtle_master",new PotionEffect(MobEffects.SLOWNESS,800,3),new PotionEffect(MobEffects.RESISTANCE,800,2));
  type("strong_turtle_master","turtle_master",new PotionEffect(MobEffects.SLOWNESS,400,5),new PotionEffect(MobEffects.RESISTANCE,400,3));
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean has(Entity e,int index) { return com.glowingfederal.moderncombatmovement.config.Settings.kit(e.world.isRemote) && e instanceof EntityLivingBase && EFFECTS[index]!=null && ((EntityLivingBase)e).isPotionActive(EFFECTS[index]); }
 public static double gravity(EntityLivingBase entity,double base){return entity.motionY<=0&&has(entity,4)?Math.min(base,.01):base;}
 public static boolean weavingWeb(Entity e) { return has(e,0) && e.getEntityData().getBoolean("MCMWeavingWeb"); }
 private static PotionType type(String id,String base,PotionEffect... effects){
  net.minecraft.util.ResourceLocation vanilla=new net.minecraft.util.ResourceLocation("minecraft",id),uab=new net.minecraft.util.ResourceLocation("etfuturum",id);
  if(PotionType.REGISTRY.containsKey(vanilla))return PotionType.REGISTRY.getObject(vanilla);
  if(PotionType.REGISTRY.containsKey(uab))return PotionType.REGISTRY.getObject(uab);
  PotionType type=new PotionType(base,effects).setRegistryName("moderncombatmovement",id);net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(PotionType.class).register(type);return type;
 }
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.LOWEST) public void death(LivingDeathEvent e) {
   EntityLivingBase p=e.getEntityLiving(); World w=p.world; if(w.isRemote || e.isCanceled()) return;
   if(has(p,0) && (p instanceof EntityPlayer || w.getGameRules().getBoolean("mobGriefing"))) {
    int max=2+p.getRNG().nextInt(2); Set<String> selected=new HashSet<String>();
    for(int attempt=0;attempt<15 && selected.size()<max;attempt++) {
     int x=(int)Math.floor(p.posX)+p.getRNG().nextInt(3)-1,y=(int)Math.floor(p.getEntityBoundingBox().minY)+p.getRNG().nextInt(3)-1,z=(int)Math.floor(p.posZ)+p.getRNG().nextInt(3)-1;
     String key=x+","+y+","+z;
     if(!selected.contains(key) && w.getBlockState(new net.minecraft.util.math.BlockPos(x,y,z)).getBlock().isReplaceable(w,new net.minecraft.util.math.BlockPos(x,y,z)) && w.isSideSolid(new net.minecraft.util.math.BlockPos(x,y-1,z),net.minecraft.util.EnumFacing.UP)) { selected.add(key); w.setBlockState(new net.minecraft.util.math.BlockPos(x,y,z),Blocks.WEB.getDefaultState(),3); }
    }
   }
   if(has(p,1)) {
    int nearby=0;for(Object candidate:w.getEntitiesWithinAABB(EntitySlime.class,p.getEntityBoundingBox().grow(2,2,2)))if(candidate!=p)nearby++;
    int limit=w.getGameRules().getInt("maxEntityCramming"); int count=limit<=0 ? 2 : Math.min(2,Math.max(0,limit-nearby));
    for(int i=0;i<count;i++) { EntitySlime slime=new EntitySlime(w); ((SlimeAccess)slime).mcm$size(2); slime.setLocationAndAngles(p.posX,p.posY+.5,p.posZ,p.getRNG().nextFloat()*360,0); w.spawnEntity(slime); }
   }
   if(has(p,3)) Gust.burst(w,p,p.posX,p.posY+p.height/2,p.posZ,3+p.getRNG().nextFloat()*2);
  }
  @SubscribeEvent(priority=EventPriority.LOWEST) public void hurt(LivingHurtEvent e) {
   EntityLivingBase p=e.getEntityLiving(); World w=p.world;
   if(w.isRemote || e.isCanceled() || e.getAmount()<=0 || !has(p,2) || p.getRNG().nextFloat()>.1F) return;
   int count=1+p.getRNG().nextInt(2);
   for(int i=0;i<count;i++) {
    EntitySilverfish fish=new EntitySilverfish(w); fish.setLocationAndAngles(p.posX,p.posY+p.height/2,p.posZ,p.getRNG().nextFloat()*360,0);
    double rotation=(p.getRNG().nextFloat()-.5)*Math.PI; double lx=p.getLookVec().x*.3,lz=p.getLookVec().z*.3;
    fish.motionX=lx*Math.cos(rotation)+lz*Math.sin(rotation); fish.motionZ=lz*Math.cos(rotation)-lx*Math.sin(rotation); fish.motionY=p.getLookVec().y*.45;
    if(w.spawnEntity(fish))fish.playSound(net.minecraft.init.SoundEvents.ENTITY_SILVERFISH_HURT,1,1);
   }
  }
 }
 private static final class Effect extends Potion {
  Effect(String name,int color) { super(!"slow_falling".equals(name),color); setPotionName("effect.moderncombatmovement."+name); }
 }
}
