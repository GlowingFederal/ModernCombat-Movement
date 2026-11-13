package com.glowingfederal.moderncombatmovement.render;

import net.minecraft.client.model.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.glowingfederal.moderncombatmovement.compat.Crossbows;
import com.glowingfederal.moderncombatmovement.config.Settings;
/** Native model adapter for vanilla crossbow charge/hold rotations; UAB equipment rendering is the technical reference. */
public final class CrossbowPoses {
 private CrossbowPoses() {}
 public static void apply(ModelBiped model,EntityPlayer p,float age) {
  if(!Settings.kit(p.world.isRemote))return;
  ItemStack stack=p.getHeldItemMainhand();
  boolean left=p.getPrimaryHand()==net.minecraft.util.EnumHandSide.LEFT;
  if(!Crossbows.known(stack)) { stack=p.getHeldItemOffhand();left=!left; }

  if(!Crossbows.known(stack))return;
  boolean using=p.isHandActive() && p.getActiveItemStack()==stack;
  if(!using && !Crossbows.charged(stack))return;
  ModelRenderer main=left?model.bipedLeftArm:model.bipedRightArm,other=left?model.bipedRightArm:model.bipedLeftArm;
  float sign=left?-1:1;
  if(using) {
   float t=Math.max(0,Math.min(1,(p.getItemInUseMaxCount()+Math.max(0,Math.min(1,age-p.ticksExisted)))/Crossbows.chargeTime(stack)));
   main.rotateAngleY=-.8F*sign;main.rotateAngleX=-.97079635F;
   other.rotateAngleY=(.4F+.45F*t)*sign;other.rotateAngleX=-.97079635F+(-1.5707964F+.97079635F)*t;
  } else {
   main.rotateAngleY=-.3F*sign+model.bipedHead.rotateAngleY;other.rotateAngleY=.6F*sign+model.bipedHead.rotateAngleY;
   main.rotateAngleX=-1.5707964F+model.bipedHead.rotateAngleX+.1F;other.rotateAngleX=-1.5F+model.bipedHead.rotateAngleX;
  }
  main.rotateAngleX+=(float)Math.sin(age*.067F)*.05F*sign;other.rotateAngleX-=(float)Math.sin(age*.067F)*.05F*sign;
 }
}
