package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.render.*;
import com.glowingfederal.moderncombatmovement.compat.*;
@Mixin(value=ItemRenderer.class,remap=false)
public abstract class ModernHandMixin {
 @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V","func_187457_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$hand(AbstractClientPlayer p,float partial,float pitch,EnumHand hand,float swing,ItemStack stack,float equip,CallbackInfo ci){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(true))return;
  boolean active=p.isHandActive();ItemStack use=p.getActiveItemStack();
  if(active&&(use.getItem() instanceof net.minecraft.item.ItemBow||Crossbows.known(use))&&p.getActiveHand()!=hand){ci.cancel();return;}
  if(hand==EnumHand.OFF_HAND&&Crossbows.known(p.getHeldItemMainhand())&&Crossbows.charged(p.getHeldItemMainhand())&&!active){ci.cancel();return;}
  if(!Crossbows.known(stack)&&ModernEquipment.spear(stack)==null)return;
  ci.cancel();boolean left=(hand==EnumHand.MAIN_HAND?p.getPrimaryHand():p.getPrimaryHand().opposite())==EnumHandSide.LEFT;int sign=left?-1:1;
  GlStateManager.pushMatrix();try{
   if(Crossbows.known(stack))CrossbowHandTransforms.apply(HandMatrices.INSTANCE,sign,equip,swing,active&&p.getActiveHand()==hand,p.getItemInUseMaxCount()+partial-1,Crossbows.chargeTime(stack),Crossbows.charged(stack),hand==EnumHand.MAIN_HAND);
   else SpearAnimationMath.firstPerson(HandMatrices.INSTANCE,ModernEquipment.spear(stack),sign,equip,swing,active&&p.getActiveHand()==hand?p.getItemInUseMaxCount()+partial:0);
   ((ItemRenderer)(Object)this).renderItemSide(p,stack,left?ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND:ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,left);
  }finally{GlStateManager.popMatrix();}
 }
}
