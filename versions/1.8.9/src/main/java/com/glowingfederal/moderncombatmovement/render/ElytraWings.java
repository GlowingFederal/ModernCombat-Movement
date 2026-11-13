package com.glowingfederal.moderncombatmovement.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
/** Native wing geometry and per-tick ElytraAnimationState; no frame-rate-dependent wing simulation. */
public final class ElytraWings {
 private static final ResourceLocation DEFAULT=new ResourceLocation("moderncombatmovement:textures/entity/elytra.png"),GLINT=new ResourceLocation("textures/misc/enchanted_item_glint.png");
 private static final Wings MODEL=new Wings();private ElytraWings(){}
 public static void render(EntityPlayer p,float partial,float scale){
  ItemStack stack=FlightHooks.chest(p);if(!FlightHooks.managed(p)||!FlightHooks.known(stack)||p.isInvisible())return;
  ResourceLocation texture=DEFAULT;
  if(p instanceof AbstractClientPlayer){AbstractClientPlayer client=(AbstractClientPlayer)p;
   if(client.isWearing(net.minecraft.entity.player.EnumPlayerModelParts.CAPE)&&client.getLocationCape()!=null)texture=client.getLocationCape();
  }
  int binding=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT|GL11.GL_CURRENT_BIT|GL11.GL_TEXTURE_BIT);GL11.glPushMatrix();
  try{
   GL11.glDisable(GL11.GL_CULL_FACE);GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glAlphaFunc(GL11.GL_GREATER,.1F);GL11.glColor4f(1,1,1,1);GL11.glTranslatef(0,0,.125F);
   WingAnimation animation=PlayerHooks.state(p).wings;MODEL.left.rotationPointY=MODEL.right.rotationPointY=PlayerHooks.state(p).pose==Pose.CROUCHING?3:0;
   MODEL.left.rotateAngleX=MODEL.right.rotateAngleX=animation.x(partial);MODEL.left.rotateAngleY=animation.y(partial);MODEL.right.rotateAngleY=-MODEL.left.rotateAngleY;MODEL.left.rotateAngleZ=animation.z(partial);MODEL.right.rotateAngleZ=-MODEL.left.rotateAngleZ;
   Minecraft.getMinecraft().getTextureManager().bindTexture(texture);MODEL.draw(scale);
   if(stack.hasEffect()){
    GL11.glDepthFunc(GL11.GL_EQUAL);GL11.glDepthMask(false);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_COLOR,GL11.GL_ONE);Minecraft.getMinecraft().getTextureManager().bindTexture(GLINT);GL11.glColor4f(.38F,.19F,.61F,1);
    for(int pass=0;pass<2;pass++){GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPushMatrix();try{GL11.glLoadIdentity();GL11.glScalef(.33333334F,.33333334F,.33333334F);GL11.glRotatef(30-pass*60,0,0,1);GL11.glTranslatef(0,(p.ticksExisted+partial)*(.001F+pass*.003F)*20,0);GL11.glMatrixMode(GL11.GL_MODELVIEW);MODEL.draw(scale);}finally{GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);}}
   }
  }finally{GL11.glPopMatrix();GL11.glPopAttrib();net.minecraft.client.renderer.GlStateManager.bindTexture(binding);}
 }
 public static void rotate(EntityPlayer p,float partial){
  if(!FlightHooks.managed(p)||!FlightHooks.flying(p))return;
  
  float ticks=PlayerHooks.state(p).flightTicks+partial,progress=Math.min(1,ticks*ticks/100);GL11.glRotatef(progress*(-90-p.rotationPitch),1,0,0);
  net.minecraft.util.Vec3 look=p.getLook(partial);double x=look.xCoord,z=look.zCoord,speed=p.motionX*p.motionX+p.motionZ*p.motionZ,direction=x*x+z*z;
  if(speed>0&&direction>0){double dot=(p.motionX*x+p.motionZ*z)/Math.sqrt(speed*direction),cross=p.motionX*z-p.motionZ*x;GL11.glRotatef((float)(Math.signum(cross)*Math.acos(Math.max(-1,Math.min(1,dot)))*180/Math.PI),0,1,0);}
 }
 private static final class Wings extends ModelBase {
  final ModelRenderer left=new ModelRenderer(this,22,0),right=new ModelRenderer(this,22,0);
  Wings(){textureWidth=64;textureHeight=32;left.addBox(-10,0,0,10,20,2,1);right.mirror=true;right.addBox(0,0,0,10,20,2,1);left.rotationPointX=5;right.rotationPointX=-5;}
  void draw(float scale){left.render(scale);right.render(scale);}
 }
}
