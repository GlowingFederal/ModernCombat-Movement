/* Native RenderDragon crystal-beam geometry, adapted to the crystal end of the beam. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.compat.*;
public final class CrystalBeamRender {
 private static final ResourceLocation BEAM=new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
 private CrystalBeamRender(){}
 public static void draw(EntityEnderCrystal crystal,double x,double y,double z,float partial){
  if(!Crystals.known(crystal)||!((CrystalState)crystal).mcm$beamActive())return;
  CrystalState state=(CrystalState)crystal;
  double dx=state.mcm$beamX()+.5-crystal.posX,dy=state.mcm$beamY()+.5-crystal.posY,dz=state.mcm$beamZ()+.5-crystal.posZ;
  float length=(float)Math.sqrt(dx*dx+dy*dy+dz*dz),horizontal=(float)Math.sqrt(dx*dx+dz*dz);
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_TEXTURE_BIT|GL11.GL_POLYGON_BIT);GL11.glPushMatrix();
  try{
   GL11.glTranslated(x,y,z);GL11.glRotatef((float)(-Math.atan2(dz,dx)*180/Math.PI)-90,0,1,0);GL11.glRotatef((float)(-Math.atan2(horizontal,dy)*180/Math.PI)-90,1,0,0);
   GL11.glDisable(GL11.GL_LIGHTING);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glShadeModel(GL11.GL_SMOOTH);Minecraft.getMinecraft().getTextureManager().bindTexture(BEAM);
   float endV=-(crystal.ticksExisted+partial)*.01F,startV=length/32-(crystal.ticksExisted+partial)*.01F;
   net.minecraft.client.renderer.Tessellator tess=net.minecraft.client.renderer.Tessellator.getInstance();net.minecraft.client.renderer.WorldRenderer buffer=tess.getWorldRenderer();buffer.begin(5,net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR);
   for(int i=0;i<=8;i++){
    float a=(float)Math.sin(i%8*Math.PI*2/8)*.75F,b=(float)Math.cos(i%8*Math.PI*2/8)*.75F,u=i%8/8F;
    buffer.pos(a*.2,b*.2,0).tex(u,startV).color(0,0,0,255).endVertex();buffer.pos(a,b,length).tex(u,endV).color(255,255,255,255).endVertex();
   }
   tess.draw();
  }finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }
}
