package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Optional movement ownership. Combatives keeps its own hitbox, camera, swimming and crawling implementation. */
public final class MovementCompat {
 private static Class<?> api;
 private static java.lang.reflect.Method pose,swimming;
 private static boolean resolved;
 private MovementCompat(){}
 public static boolean exclusive(EntityPlayer player){PlayerState state=PlayerHooks.state(player);return state!=null&&(state.spinTicks>0||com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(player));}
 public static boolean owns(EntityPlayer player){
  if(PlayerHooks.state(player)==null)return false;
  if(!resolved){resolved=true;try{api=Class.forName("com.glowingfederal.combatives.entity.player.ICombativesPlayerPose");pose=api.getMethod("getPose");swimming=api.getMethod("isSwimming");}catch(ClassNotFoundException absent){api=null;}catch(ReflectiveOperationException changed){throw new IllegalStateException("Unsupported Combatives pose API",changed);}}
  return api!=null&&api.isInstance(player)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(player)&&PlayerHooks.state(player).spinTicks<=0;
 }
 public static void mirror(EntityPlayer player){
  if(!owns(player))return;
  try{PlayerState state=PlayerHooks.state(player);String name=((Enum<?>)pose.invoke(player)).name();state.pose=name.equals("SWIMMING")?Pose.LOW:name.equals("CROUCHING")?Pose.CROUCHING:Pose.STANDING;state.swimming=Boolean.TRUE.equals(swimming.invoke(player));state.ownedDimensions=false;}
  catch(ReflectiveOperationException failure){throw new IllegalStateException("Combatives pose bridge failed",failure);}
 }
}
