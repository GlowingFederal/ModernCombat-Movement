package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.EntityLivingBase;
/** Context only for the native pearl event; other mods' teleport events retain their owner. */
public final class PearlTeleport {
 private static final ThreadLocal<Boolean> EVENT=new ThreadLocal<Boolean>();
 private PearlTeleport(){}
 public static boolean active(){return Boolean.TRUE.equals(EVENT.get());}
 public static boolean post(net.minecraftforge.fml.common.eventhandler.EventBus bus,net.minecraftforge.fml.common.eventhandler.Event event){Boolean previous=EVENT.get();EVENT.set(Boolean.TRUE);try{return bus.post(event);}finally{if(previous==null)EVENT.remove();else EVENT.set(previous);}}
 public static void arrived(EntityLivingBase living,double fromX,double fromY,double fromZ){
  living.motionX=living.motionY=living.motionZ=0;living.fallDistance=0;living.velocityChanged=true;
  com.glowingfederal.moderncombatmovement.combat.SpearMechanics.sync(living);ItemSounds.play(living,"entity.player.teleport",1,1);
  
 }
}
