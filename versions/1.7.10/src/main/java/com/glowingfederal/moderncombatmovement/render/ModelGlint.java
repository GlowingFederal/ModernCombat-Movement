/* Native enchanted equipment pass reused from the existing UAB-derived equipment renderer. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
final class ModelGlint {
 private static final ResourceLocation GLINT=new ResourceLocation("textures/misc/enchanted_item_glint.png");
 private ModelGlint(){}
 static void render(boolean foil,float age,float textureScale,Runnable draw,ResourceLocation texture){
  if(!foil)return;
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT|GL11.GL_TEXTURE_BIT);
  try{
   GL11.glDepthFunc(GL11.GL_EQUAL);GL11.glDepthMask(false);GL11.glDisable(GL11.GL_LIGHTING);GL11.glEnable(GL11.GL_BLEND);GL11.glBlendFunc(GL11.GL_SRC_COLOR,GL11.GL_ONE);
   Minecraft.getMinecraft().getTextureManager().bindTexture(GLINT);GL11.glColor4f(.38F,.19F,.61F,1);
   for(int pass=0;pass<2;pass++){
    GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPushMatrix();
    try{GL11.glLoadIdentity();GL11.glScalef(textureScale,textureScale,textureScale);GL11.glRotatef(30-pass*60,0,0,1);GL11.glTranslatef(0,age*(.001F+pass*.003F)*20,0);GL11.glMatrixMode(GL11.GL_MODELVIEW);draw.run();}
    finally{GL11.glMatrixMode(GL11.GL_TEXTURE);GL11.glPopMatrix();GL11.glMatrixMode(GL11.GL_MODELVIEW);}
   }
  }finally{GL11.glPopAttrib();Minecraft.getMinecraft().getTextureManager().bindTexture(texture);}
 }
}
