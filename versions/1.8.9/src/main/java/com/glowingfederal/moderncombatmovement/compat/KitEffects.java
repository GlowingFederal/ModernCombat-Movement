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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.event.entity.living.*;
public final class KitEffects {
 public static final String[] NAMES={"weaving","oozing","infested","wind_charged","slow_falling"};
 public static final Potion[] EFFECTS=new Potion[5];
 private static final int[] COLORS={7891290,10092451,9214860,12438015,15978425};
 public static final PotionIdRegistry IDS=new PotionIdRegistry("moderncombatmovement");
 private KitEffects() {}
 public static void register(File file) {
  Configuration c=new Configuration(file);c.load();Map<String,Integer> requests=new TreeMap<String,Integer>();
  int[] previousDefaults=new int[]{31,30,29,28};
  for(int i=0;i<4;i++){int id=c.get("Compatibility",NAMES[i]+"PotionId",previousDefaults[i],"Existing reserved potion ID; preserve this value for saved worlds and use the same value on both sides.").getInt();if(id<1||id>PotionIdRegistry.maximumSupportedId()||Potion.potionTypes[id]!=null)throw new IllegalStateException("Potion ID occupied or unsupported for "+NAMES[i]+": "+id+"; preserve matching saved configuration");EFFECTS[i]=new Effect(id,NAMES[i],COLORS[i]);}
  for(int i=4;i<NAMES.length;i++){int preferred=c.get("Compatibility",NAMES[i]+"PotionId",-1,"New effect: -1 uses a persistent allocation in config/legacy-potion-ids.properties. Keep that file with the pack and saved worlds.").getInt();if(preferred==0)throw new IllegalStateException("Potion zero is reserved; use -1 or an available supported ID for "+NAMES[i]);requests.put(NAMES[i],preferred);}
  if(!Content.uab)requests.put("glowing",c.get("Compatibility","glowingPotionId",-1,"Standalone spectral-arrow effect; -1 uses the persistent legacy potion allocation file.").getInt());
  IDS.reserve(requests);IDS.allocate();
  if(!Content.uab){Ammunition.glowing=new Effect(IDS.id("glowing"),"glowing",9740385);IDS.track("glowing",Ammunition.glowing);}
  else try{Object effect=Class.forName("ganymedes01.etfuturum.potion.ModPotions").getField("glowing").get(null);if(effect instanceof Potion)Ammunition.glowing=(Potion)effect;}catch(ReflectiveOperationException absent){throw new IllegalStateException("Unsupported UAB glowing effect",absent);}
  for(int i=4;i<NAMES.length;i++){EFFECTS[i]=new Effect(IDS.id(NAMES[i]),NAMES[i],COLORS[i]);IDS.track(NAMES[i],EFFECTS[i]);}
  if(c.hasChanged())c.save();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(IDS);net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void verifyRegistrations(){Ammunition.resolveGlowing();IDS.verify();for(Potion potion:EFFECTS)if(potion==null||potion.id<0||potion.id>=Potion.potionTypes.length||Potion.potionTypes[potion.id]!=potion)throw new IllegalStateException("An MCM potion registration was overwritten; restore matching saved potion IDs");}
 public static boolean has(Entity e,int index) { return com.glowingfederal.moderncombatmovement.config.Settings.kit(e.worldObj.isRemote) && e instanceof EntityLivingBase && EFFECTS[index]!=null && ((EntityLivingBase)e).isPotionActive(EFFECTS[index]); }
 public static double gravity(EntityLivingBase entity,double base){return entity.motionY<=0&&has(entity,4)?Math.min(base,.01):base;}
 public static boolean weavingWeb(Entity e) { return has(e,0) && e.getEntityData().getBoolean("MCMWeavingWeb"); }
 public static void addPotions(List<ItemStack> list) {
  addPotions(Items.potionitem,list);
 }
 public static void addPotions(Item containerItem,List<ItemStack> list) {
  for(int splash=0;splash<2;splash++){
   int meta=splash==0?0:16384;
   for(int i=0;i<EFFECTS.length;i++)append(list,containerItem,meta,NAMES[i],new PotionEffect(EFFECTS[i].id,i==4?1800:3600));
   append(list,containerItem,meta,"slow_falling",new PotionEffect(EFFECTS[4].id,4800));
   append(list,containerItem,meta,"slowness",new PotionEffect(Potion.moveSlowdown.id,400,3));
   append(list,containerItem,meta,"turtle_master",new PotionEffect(Potion.moveSlowdown.id,400,3),new PotionEffect(Potion.resistance.id,400,2));
   append(list,containerItem,meta,"turtle_master",new PotionEffect(Potion.moveSlowdown.id,800,3),new PotionEffect(Potion.resistance.id,800,2));
   append(list,containerItem,meta,"turtle_master",new PotionEffect(Potion.moveSlowdown.id,400,5),new PotionEffect(Potion.resistance.id,400,3));
  }
 }
 private static void append(List<ItemStack> list,Item item,int meta,String name,PotionEffect... values){
  ItemStack stack=new ItemStack(item,1,meta);NBTTagCompound tag=new NBTTagCompound();NBTTagList effects=new NBTTagList();
  for(PotionEffect value:values){NBTTagCompound effect=new NBTTagCompound();value.writeCustomPotionEffectToNBT(effect);effects.appendTag(effect);}
  tag.setTag("CustomPotionEffects",effects);tag.setString("MCMPotion",name);stack.setTagCompound(tag);list.add(stack);
 }
 public static final class Events {
  @SubscribeEvent(priority=EventPriority.LOWEST) public void death(LivingDeathEvent e) {
   EntityLivingBase p=e.entityLiving; World w=p.worldObj; if(w.isRemote || e.isCanceled()) return;
   if(has(p,0) && (p instanceof EntityPlayer || w.getGameRules().getBoolean("mobGriefing"))) {
    int max=2+p.getRNG().nextInt(2); Set<String> selected=new HashSet<String>();
    for(int attempt=0;attempt<15 && selected.size()<max;attempt++) {
     int x=(int)Math.floor(p.posX)+p.getRNG().nextInt(3)-1,y=(int)Math.floor(p.getEntityBoundingBox().minY)+p.getRNG().nextInt(3)-1,z=(int)Math.floor(p.posZ)+p.getRNG().nextInt(3)-1;
     String key=x+","+y+","+z;
     if(!selected.contains(key) && w.getBlockState(new net.minecraft.util.BlockPos(x,y,z)).getBlock().isReplaceable(w,new net.minecraft.util.BlockPos(x,y,z)) && w.isSideSolid(new net.minecraft.util.BlockPos(x,y-1,z),net.minecraft.util.EnumFacing.UP)) { selected.add(key); w.setBlockState(new net.minecraft.util.BlockPos(x,y,z),Blocks.web.getDefaultState(),3); }
    }
   }
   if(has(p,1)) {
    int nearby=0;for(Object candidate:w.getEntitiesWithinAABB(EntitySlime.class,p.getEntityBoundingBox().expand(2,2,2)))if(candidate!=p)nearby++;
    int limit=w.getGameRules().hasRule("maxEntityCramming")?w.getGameRules().getInt("maxEntityCramming"):24; int count=limit<=0 ? 2 : Math.min(2,Math.max(0,limit-nearby));
    for(int i=0;i<count;i++) { EntitySlime slime=new EntitySlime(w); ((SlimeAccess)slime).mcm$size(2); slime.setLocationAndAngles(p.posX,p.posY+.5,p.posZ,p.getRNG().nextFloat()*360,0); w.spawnEntityInWorld(slime); }
   }
   if(has(p,3)) Gust.burst(w,p,p.posX,p.posY+p.height/2,p.posZ,3+p.getRNG().nextFloat()*2);
  }
  @SubscribeEvent(priority=EventPriority.LOWEST) public void hurt(LivingHurtEvent e) {
   EntityLivingBase p=e.entityLiving; World w=p.worldObj;
   if(w.isRemote || e.isCanceled() || e.ammount<=0 || !has(p,2) || p.getRNG().nextFloat()>.1F) return;
   int count=1+p.getRNG().nextInt(2);
   for(int i=0;i<count;i++) {
    EntitySilverfish fish=new EntitySilverfish(w); fish.setLocationAndAngles(p.posX,p.posY+p.height/2,p.posZ,p.getRNG().nextFloat()*360,0);
    double rotation=(p.getRNG().nextFloat()-.5)*Math.PI; double lx=p.getLookVec().xCoord*.3,lz=p.getLookVec().zCoord*.3;
    fish.motionX=lx*Math.cos(rotation)+lz*Math.sin(rotation); fish.motionZ=lz*Math.cos(rotation)-lx*Math.sin(rotation); fish.motionY=p.getLookVec().yCoord*.45;
    if(w.spawnEntityInWorld(fish))fish.playSound("mob.silverfish.hit",1,1);
   }
  }
 }
 private static final class Effect extends Potion {
  Effect(int id,String name,int color) { super(id,new net.minecraft.util.ResourceLocation("moderncombatmovement:"+name),!"slow_falling".equals(name)&&!"glowing".equals(name),color); setPotionName("effect.moderncombatmovement."+name); }
 }
}
