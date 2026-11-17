package com.glowingfederal.moderncombatmovement.movement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
/** Modern Player.maybeBackOffFromEdge support-slab test, using the target collision engine. */
public final class GroundMovement {
 private GroundMovement(){}
 public static boolean owns(EntityPlayer p){return !p.noClip&&PlayerHooks.active(p);}
 private static boolean unsupported(EntityPlayer p,double x,double z,double depth){
  AxisAlignedBB b=p.getEntityBoundingBox();
  return p.world.getCollisionBoxes(p,new AxisAlignedBB(b.minX+1e-7+x,b.minY-depth-1e-7,b.minZ+1e-7+z,b.maxX-1e-7+x,b.minY,b.maxZ-1e-7+z)).isEmpty();
 }
 public static double edge(EntityPlayer p,double x,double y,double z,boolean xAxis){
  double original=xAxis?x:z;
  if(!owns(p)||!p.isSneaking()||y>0||FlightHooks.flying(p)||com.glowingfederal.moderncombatmovement.compat.Tridents.spinning(p))return original;
  double depth=p.stepHeight;
  if(!p.onGround&&!(p.fallDistance<depth&&!unsupported(p,0,0,depth-p.fallDistance)))return original;
  while(x!=0&&unsupported(p,x,0,depth))x=reduce(x);
  while(z!=0&&unsupported(p,0,z,depth))z=reduce(z);
  while(x!=0&&z!=0&&unsupported(p,x,z,depth)){x=reduce(x);z=reduce(z);}
  return xAxis?x:z;
 }
 private static double reduce(double value){return Math.abs(value)<=.05?0:value-Math.signum(value)*.05;}
}
