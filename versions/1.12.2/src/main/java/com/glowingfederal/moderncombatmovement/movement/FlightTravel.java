package com.glowingfederal.moderncombatmovement.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.ModernCombatMovement;
/** Air flight has one owner. Native fluid branches and collision resolution remain active. */
public final class FlightTravel {
 private static final FlightWallDamage WALL=new FlightWallDamage();private FlightTravel(){}
 public static boolean travel(EntityPlayer p){
  if(!FlightHooks.managed(p)||!FlightHooks.flying(p)||p.isInWater()||p.isInLava()||p.world.isRemote&&!ModernCombatMovement.proxy.local(p))return false;
  if(p.isOnLadder()){FlightHooks.stop(p);return false;}
  double beforeX=p.posX,beforeZ=p.posZ,horizontal=Math.sqrt(p.motionX*p.motionX+p.motionZ*p.motionZ);
  Vec3d look=FlightHooks.look(p);float pitch=p.rotationPitch*(float)(Math.PI/180);
  FlightMotion velocity=PlayerHooks.state(p).flightMotion;velocity.set(p.motionX,p.motionY,p.motionZ);
  FlightMath.travel(velocity,look.x,look.z,pitch,net.minecraft.util.math.MathHelper.sin(pitch),FlightHooks.gravity(p));
  p.motionX=velocity.x;p.motionY=velocity.y;p.motionZ=velocity.z;
  p.move(net.minecraft.entity.MoverType.SELF,p.motionX,p.motionY,p.motionZ);
  if(!p.world.isRemote&&p.collidedHorizontally){float damage=FlightMath.wallDamage(horizontal,Math.sqrt(p.motionX*p.motionX+p.motionZ*p.motionZ));if(damage>0)p.attackEntityFrom(WALL,damage);}
  p.prevLimbSwingAmount=p.limbSwingAmount;float limb=Math.min(1,(float)Math.sqrt((p.posX-beforeX)*(p.posX-beforeX)+(p.posZ-beforeZ)*(p.posZ-beforeZ))*4);p.limbSwingAmount+=(limb-p.limbSwingAmount)*.4F;p.limbSwing+=p.limbSwingAmount;return true;
 }
}
