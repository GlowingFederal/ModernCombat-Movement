package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.model.*;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.compat.Tridents;
/** The modern THROW_SPEAR pose affects only the actual active arm. */
public final class TridentPoses {
 private TridentPoses(){}
 public static boolean active(EntityPlayer p){return Tridents.known(p.getItemInUse());}
 public static void apply(ModelBiped model,EntityPlayer p){
  if(!active(p))return;
  boolean left=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p);
  
  ModelRenderer arm=left?model.bipedLeftArm:model.bipedRightArm;arm.rotateAngleX=arm.rotateAngleX*.5F-(float)Math.PI;arm.rotateAngleY=0;
 }
}
