package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.model.*;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.compat.Tridents;
/** The modern THROW_SPEAR pose affects only the actual active arm. */
public final class TridentPoses {
 private TridentPoses(){}
 public static boolean active(EntityPlayer p){return Tridents.known(p.getActiveItemStack());}
 public static void apply(ModelBiped model,EntityPlayer p){
  if(!active(p))return;
  boolean left=p.getActiveHand()==net.minecraft.util.EnumHand.OFF_HAND;
  if(p.getPrimaryHand()==net.minecraft.util.EnumHandSide.LEFT)left=!left;
  ModelRenderer arm=left?model.bipedLeftArm:model.bipedRightArm;arm.rotateAngleX=arm.rotateAngleX*.5F-(float)Math.PI;arm.rotateAngleY=0;
 }
}
