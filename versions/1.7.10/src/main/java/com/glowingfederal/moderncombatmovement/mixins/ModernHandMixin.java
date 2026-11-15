package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.render.*;
import com.glowingfederal.moderncombatmovement.compat.*;
@Mixin(value=ItemRenderer.class,remap=false)
public abstract class ModernHandMixin {
 @Shadow(aliases="field_78453_b")private ItemStack itemToRender;
 @Shadow(aliases="field_78454_c")private float equippedProgress;
 @Shadow(aliases="field_78451_d")private float prevEquippedProgress;
 @Inject(method={"renderItemInFirstPerson","func_78440_a"},at=@At("HEAD"),cancellable=true)private void mcm$crossbow(float partial,CallbackInfo ci){
  com.glowingfederal.moderncombatmovement.render.OffhandClient.firstPerson(partial);
  Minecraft mc=Minecraft.getMinecraft();EntityPlayerSP p=mc.thePlayer;
  if(p==null||!com.glowingfederal.moderncombatmovement.config.Settings.kit(true)||(!Crossbows.known(itemToRender)&&ModernEquipment.spear(itemToRender)==null))return;
  ci.cancel();float pitch=p.prevRotationPitch+(p.rotationPitch-p.prevRotationPitch)*partial,yaw=p.prevRotationYaw+(p.rotationYaw-p.prevRotationYaw)*partial;
  GL11.glPushMatrix();GL11.glRotatef(pitch,1,0,0);GL11.glRotatef(yaw,0,1,0);RenderHelper.enableStandardItemLighting();GL11.glPopMatrix();
  GL11.glRotatef((p.rotationPitch-(p.prevRenderArmPitch+(p.renderArmPitch-p.prevRenderArmPitch)*partial))*.1F,1,0,0);
  GL11.glRotatef((p.rotationYaw-(p.prevRenderArmYaw+(p.renderArmYaw-p.prevRenderArmYaw)*partial))*.1F,0,1,0);
  int light=mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(p.posX),MathHelper.floor_double(p.posY),MathHelper.floor_double(p.posZ),0);
  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,light%65536,light/65536);
  GL11.glPushMatrix();GL11.glEnable(GL12.GL_RESCALE_NORMAL);
  try{if(Crossbows.known(itemToRender)){CrossbowHandTransforms.apply(HandMatrices.INSTANCE,1,1-(prevEquippedProgress+(equippedProgress-prevEquippedProgress)*partial),p.getSwingProgress(partial),p.getItemInUse()==itemToRender,p.getItemInUseDuration()+partial-1,Crossbows.chargeTime(itemToRender),Crossbows.charged(itemToRender),true);new CrossbowItemRenderer().renderFirstPersonModern(itemToRender,p);}else{SpearAnimationMath.firstPerson(HandMatrices.INSTANCE,ModernEquipment.spear(itemToRender),1,1-(prevEquippedProgress+(equippedProgress-prevEquippedProgress)*partial),p.getSwingProgress(partial),p.getItemInUse()==itemToRender?p.getItemInUseDuration()+partial:0);new SpearItemRenderer().renderModern(itemToRender,true);}}
  finally{GL11.glPopMatrix();GL11.glDisable(GL12.GL_RESCALE_NORMAL);RenderHelper.disableStandardItemLighting();}
 }
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityClientPlayerMP;getItemInUseCount()I"),require=0)private int mcm$mainUseMcp(net.minecraft.client.entity.EntityClientPlayerMP p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityClientPlayerMP;func_71052_bv()I"),require=0)private int mcm$mainUseSrg(net.minecraft.client.entity.EntityClientPlayerMP p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
}
