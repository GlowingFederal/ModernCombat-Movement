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
 public static final String[] NAMES={"weaving","oozing","infested","wind_charged"};
 public static final Potion[] EFFECTS=new Potion[4];
 private static final int[] COLORS={7891290,10092451,9214860,12438015};
 private KitEffects() {}
 public static void register(File file) {
  Configuration c=new Configuration(file); c.load();
  for(int i=0;i<NAMES.length;i++) {
   int preferred=c.get("Compatibility",NAMES[i]+"PotionId",31-i,"Reserved legacy potion ID; must match on client and server.").getInt();
   if(preferred<1 || preferred>=Potion.potionTypes.length || Potion.potionTypes[preferred]!=null) throw new IllegalStateException("Potion ID occupied/out of range for "+NAMES[i]+": "+preferred+"; choose an unused Compatibility ID");
   EFFECTS[i]=new Effect(preferred,NAMES[i],COLORS[i]);
  }
  if(c.hasChanged()) c.save();
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean has(Entity e,int index) { return com.glowingfederal.moderncombatmovement.config.Settings.kit(e.worldObj.isRemote) && e instanceof EntityLivingBase && EFFECTS[index]!=null && ((EntityLivingBase)e).isPotionActive(EFFECTS[index]); }
 public static boolean weavingWeb(Entity e) { return has(e,0) && e.getEntityData().getBoolean("MCMWeavingWeb"); }
 public static void addPotions(List<ItemStack> list) {
  for(int i=0;i<EFFECTS.length;i++) for(int splash=0;splash<2;splash++) {
   ItemStack item=new ItemStack(Items.potionitem,1,splash==0 ? 0 : 16384);
   NBTTagList effects=new NBTTagList(); NBTTagCompound effect=new NBTTagCompound();
   new PotionEffect(EFFECTS[i].id,3600).writeCustomPotionEffectToNBT(effect); effects.appendTag(effect);
   NBTTagCompound tag=new NBTTagCompound(); tag.setTag("CustomPotionEffects",effects); item.setTagCompound(tag); list.add(item);
  }
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
    int nearby=w.getEntitiesWithinAABB(EntitySlime.class,p.getEntityBoundingBox().expand(2,2,2)).size();
    int limit=24; int count=limit<=0 ? 2 : Math.min(2,Math.max(0,limit-nearby));
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
    w.spawnEntityInWorld(fish);
   }
  }
 }
 private static final class Effect extends Potion {
  Effect(int id,String name,int color) { super(id,new net.minecraft.util.ResourceLocation("moderncombatmovement:"+name),true,color); setPotionName("effect.moderncombatmovement."+name); }
 }
}
