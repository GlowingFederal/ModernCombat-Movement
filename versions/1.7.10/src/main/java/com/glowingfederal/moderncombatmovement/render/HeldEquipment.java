/* Shield and trident geometry adapted from UAB; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.compat.*;
public final class HeldEquipment {
 private static final ModelRenderer plate,handle;
 private static final TridentModel TRIDENT=new TridentModel();
 static{ModelBase model=new ModelBase(){};model.textureWidth=model.textureHeight=64;plate=new ModelRenderer(model,0,0);plate.addBox(-6,-11,-2,12,22,1);handle=new ModelRenderer(model,26,0);handle.addBox(-1,-3,-1,2,6,6);}
 private HeldEquipment(){}
 public static void firstPerson(EntityPlayer p,ItemStack stack,int sign,float equip,float swing,boolean active,float partial){
  Minecraft mc=Minecraft.getMinecraft();GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_TEXTURE_BIT);GL11.glPushMatrix();
  try{
   GL11.glPushMatrix();GL11.glRotatef(p.rotationPitch,1,0,0);GL11.glRotatef(p.rotationYaw,0,1,0);RenderHelper.enableStandardItemLighting();GL11.glPopMatrix();
   int light=mc.theWorld.getLightBrightnessForSkyBlocks(net.minecraft.util.MathHelper.floor_double(p.posX),net.minecraft.util.MathHelper.floor_double(p.posY),net.minecraft.util.MathHelper.floor_double(p.posZ),0);OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,light%65536,light/65536);GL11.glColor4f(1,1,1,1);GL11.glEnable(32826);
   if(Shields.known(stack)){
    ShieldHandTransforms.apply(HandMatrices.INSTANCE,sign,equip,swing,active);mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/entity/shield_base_nopattern.png"));plate.render(.0625F);handle.render(.0625F);ModelGlint.render(stack.isItemEnchanted(),p.ticksExisted,.33333334F,()->{plate.render(.0625F);handle.render(.0625F);},new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/entity/shield_base_nopattern.png"));
   }else{
    TridentHandTransforms.apply(HandMatrices.INSTANCE,sign,equip,swing,active,active?p.getItemInUseDuration()+partial-1:0,com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).spinTicks>0);
    GL11.glTranslatef((sign>0?-3:-13)/16F,17/16F,1/16F);GL11.glRotatef(-90,0,1,0);GL11.glRotatef(25,0,0,1);GL11.glTranslatef(-.5F,-.5F,-.5F);GL11.glScalef(1,-1,-1);mc.getTextureManager().bindTexture(TridentModel.TEXTURE);TRIDENT.render();ModelGlint.render(stack.isItemEnchanted(),p.ticksExisted,.33333334F,()->TRIDENT.render(),TridentModel.TEXTURE);
   }
  }finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }

 /** Called at the arm attachment, before any legacy sprite matrix has been applied. */
 public static void thirdPerson(EntityPlayer p,ItemStack stack,int sign,boolean active){
  Minecraft mc=Minecraft.getMinecraft();
  GL11.glRotatef(-90,1,0,0);GL11.glRotatef(180,0,1,0);GL11.glTranslatef(sign/16F,.125F,-.625F);
  if(Shields.known(stack)){
   float x=sign>0?(active?3.51F:10.51F):(active?13.51F:10.51F),y=sign>0?(active?11:6):(active?3:6),z=sign>0?(active?-2:-4):(active?5:12);
   GL11.glTranslatef(sign*x/16F,y/16F,z/16F);if(active)GL11.glRotatef(45,1,0,0);GL11.glRotatef(sign*(active?135:90),0,1,0);GL11.glTranslatef(-.5F,-.5F,-.5F);GL11.glScalef(1,-1,-1);mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/entity/shield_base_nopattern.png"));plate.render(.0625F);handle.render(.0625F);ModelGlint.render(stack.isItemEnchanted(),p.ticksExisted,.33333334F,()->{plate.render(.0625F);handle.render(.0625F);},new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/entity/shield_base_nopattern.png"));
  }else{
   GL11.glTranslatef(sign*(sign>0?11:3)/16F,17/16F,(sign>0?-2:12)/16F);GL11.glRotatef(sign*60,0,1,0);GL11.glTranslatef(-.5F,-.5F,-.5F);GL11.glScalef(1,-1,-1);mc.getTextureManager().bindTexture(TridentModel.TEXTURE);TRIDENT.render();ModelGlint.render(stack.isItemEnchanted(),p.ticksExisted,.33333334F,()->TRIDENT.render(),TridentModel.TEXTURE);
  }
 }
}
