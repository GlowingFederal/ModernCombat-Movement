/* Native swimming cycle, using the legacy biped adaptation inspected in Combatives. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.pose.*;
public final class SwimPoses {
 private static final float[] STROKE=new float[5];
 private SwimPoses(){}
 public static void apply(ModelBiped model,EntityPlayer player,float limb,float movement,float age){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.rendering||com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(player))return;
  PlayerState state=PlayerHooks.state(player);float blend=state.swim(Math.max(0,Math.min(1,age-player.ticksExisted)));if(blend<=0||state.spinTicks>0||com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(player))return;
  if(!state.swimming&&!player.isInWater()){CrawlPoses.apply(model,limb,movement,blend);return;}
  SwimAnimationMath.stroke(limb,STROKE);float arm=model.onGround>0?0:blend,pi=(float)Math.PI;
  model.bipedLeftArm.rotateAngleX=SwimAnimationMath.angle(model.bipedLeftArm.rotateAngleX,STROKE[0],arm);model.bipedRightArm.rotateAngleX=lerp(model.bipedRightArm.rotateAngleX,STROKE[0],arm);
  model.bipedLeftArm.rotateAngleY=SwimAnimationMath.angle(model.bipedLeftArm.rotateAngleY,pi,arm);model.bipedRightArm.rotateAngleY=lerp(model.bipedRightArm.rotateAngleY,pi,arm);
  model.bipedLeftArm.rotateAngleZ=SwimAnimationMath.angle(model.bipedLeftArm.rotateAngleZ,STROKE[1],arm);model.bipedRightArm.rotateAngleZ=lerp(model.bipedRightArm.rotateAngleZ,STROKE[2],arm);
  model.bipedLeftLeg.rotateAngleX=lerp(model.bipedLeftLeg.rotateAngleX,STROKE[3],blend);model.bipedRightLeg.rotateAngleX=lerp(model.bipedRightLeg.rotateAngleX,STROKE[4],blend);
  if(state.swimming)model.bipedHead.rotateAngleX=SwimAnimationMath.angle(model.bipedHead.rotateAngleX,-pi/4,blend);
  model.bipedHeadwear.rotateAngleX=model.bipedHead.rotateAngleX;
 }
 private static float lerp(float from,float to,float amount){return from+(to-from)*amount;}
}
