package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.EntityLivingBase;
/** Context only for the native pearl event; other mods' teleport events retain their owner. */
public final class PearlTeleport {
 private static final ThreadLocal<Boolean> EVENT=new ThreadLocal<Boolean>();
 private PearlTeleport(){}
 public static boolean active(){return Boolean.TRUE.equals(EVENT.get());}
 public static boolean post(cpw.mods.fml.common.eventhandler.EventBus bus,cpw.mods.fml.common.eventhandler.Event event){Boolean previous=EVENT.get();EVENT.set(Boolean.TRUE);try{return bus.post(event);}finally{if(previous==null)EVENT.remove();else EVENT.set(previous);}}
 public static void arrived(EntityLivingBase living,double fromX,double fromY,double fromZ){
  living.motionX=living.motionY=living.motionZ=0;living.fallDistance=0;living.velocityChanged=true;
  com.glowingfederal.moderncombatmovement.combat.SpearMechanics.sync(living);ItemSounds.play(living,"entity.player.teleport",1,1);
  if(Content.uab&&living instanceof net.minecraft.entity.player.EntityPlayer&&living.getRNG().nextFloat()<.05F&&living.worldObj.getGameRules().getGameRuleBooleanValue("doMobSpawning")&&WorldSpawnRules.hostile(living.worldObj)&&living.worldObj.difficultySetting!=net.minecraft.world.EnumDifficulty.PEACEFUL){
   try{
    if(!Class.forName("ganymedes01.etfuturum.configuration.configs.ConfigEntities").getField("enableEndermite").getBoolean(null))return;
    Class<?> type=Class.forName("ganymedes01.etfuturum.entities.EntityEndermite");net.minecraft.entity.Entity mite=(net.minecraft.entity.Entity)type.getConstructor(net.minecraft.world.World.class).newInstance(living.worldObj);
    mite.setLocationAndAngles(fromX,fromY,fromZ,living.rotationYaw,living.rotationPitch);if(living.worldObj.spawnEntityInWorld(mite)){type.getMethod("setSpawnedByPlayer",boolean.class).invoke(mite,true);type.getMethod("aggroEndermen",int.class).invoke(mite,64);}
   }catch(ReflectiveOperationException failure){throw new IllegalStateException("UAB endermite adapter changed",failure);}
  }
 }
}
