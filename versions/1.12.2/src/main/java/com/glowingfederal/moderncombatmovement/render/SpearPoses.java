package com.glowingfederal.moderncombatmovement.render;

import net.minecraft.client.model.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
import com.glowingfederal.moderncombatmovement.combat.*;
import com.glowingfederal.moderncombatmovement.pose.*;
/** SpearAnimations.thirdPersonHandUse/AttackHand adapted to the native biped model. */
public final class SpearPoses {
 private SpearPoses(){}
 public static boolean owned(EntityPlayer p){return com.glowingfederal.moderncombatmovement.config.Settings.rendering&&ModernEquipment.enabled(p)&&ModernEquipment.spear(CombatHooks.held(p))!=null;}
 public static void apply(ModelBiped model,EntityPlayer p,float age,float swing){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.rendering||!ModernEquipment.enabled(p))return;
  arm(model,p,CombatHooks.held(p),p.getPrimaryHand()==net.minecraft.util.EnumHandSide.LEFT,age,swing);
  arm(model,p,p.getHeldItemOffhand(),p.getPrimaryHand()!=net.minecraft.util.EnumHandSide.LEFT,age,0);
 }
 private static void arm(ModelBiped model,EntityPlayer p,ItemStack stack,boolean left,float age,float swing){
  SpearSpec spec=ModernEquipment.spear(stack);if(spec==null)return;float sign=left?-1:1,rad=(float)(Math.PI/180);
  ModelRenderer arm=left?model.bipedLeftArm:model.bipedRightArm;
  arm.rotateAngleY=Math.max(-60*rad,Math.min(60*rad,-.1F*sign+model.bipedHead.rotateAngleY));
  arm.rotateAngleX=-(float)Math.PI/2+model.bipedHead.rotateAngleX+.8F;
  if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p)||PlayerHooks.state(p).swimming||p.isElytraFlying())arm.rotateAngleX-=.9599311F;
  arm.rotateAngleX=Math.max(-120*rad,Math.min(30*rad,arm.rotateAngleX));
  if(ModernEquipment.spearUsing(p)&&p.getActiveItemStack()==stack){float ticks=p.getItemInUseMaxCount()+Math.max(0,Math.min(1,age-p.ticksExisted));SpearAnimationMath.Use u=new SpearAnimationMath.Use(spec,ticks);
   arm.rotateAngleY+=-sign*u.fast*rad*u.intensity;arm.rotateAngleZ+=-sign*u.slow*rad*u.intensity*.5F;
   arm.rotateAngleX+=rad*(-40*u.start+30*u.middle-20*u.end+20*u.lower+10*u.back+.6F*u.slow*u.intensity);
  }
  if(swing>0){model.bipedBody.rotateAngleY=(float)Math.sin(Math.sqrt(swing)*Math.PI*2)*.2F*sign;
   model.bipedRightArm.rotationPointZ=(float)Math.sin(model.bipedBody.rotateAngleY)*5;model.bipedRightArm.rotationPointX=-(float)Math.cos(model.bipedBody.rotateAngleY)*5;
   model.bipedLeftArm.rotationPointZ=-(float)Math.sin(model.bipedBody.rotateAngleY)*5;model.bipedLeftArm.rotationPointX=(float)Math.cos(model.bipedBody.rotateAngleY)*5;
   model.bipedRightArm.rotateAngleY-=model.bipedBody.rotateAngleY;model.bipedLeftArm.rotateAngleY-=model.bipedBody.rotateAngleY;model.bipedLeftArm.rotateAngleX-=model.bipedBody.rotateAngleY;
   float a=SpearAnimationMath.inOutSine(SpearAnimationMath.progress(swing,0,.05F)),b=SpearAnimationMath.inQuad(SpearAnimationMath.progress(swing,.05F,.2F)),c=SpearAnimationMath.inOutExpo(SpearAnimationMath.progress(swing,.4F,1));arm.rotateAngleX+=(90*a-120*b+30*c)*rad;
  }
  if(PlayerHooks.state(p).pose==Pose.CROUCHING)arm.rotateAngleX+=.4F;
  arm.rotateAngleX+=(float)Math.sin(age*.067F)*.05F*sign;
 }
}
