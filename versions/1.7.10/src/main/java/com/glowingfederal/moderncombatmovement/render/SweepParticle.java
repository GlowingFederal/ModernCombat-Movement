/* Native 1.12 ParticleSweepAttack, adapted to the legacy custom-particle pass. */
package com.glowingfederal.moderncombatmovement.render;
public final class SweepParticle extends net.minecraft.client.particle.EntityFX {
 private static final net.minecraft.util.ResourceLocation TEXTURE=new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/entity/sweep.png");
 public SweepParticle(net.minecraft.world.World world,double x,double y,double z){super(world,x,y,z);motionX=motionY=motionZ=0;particleMaxAge=4;float colour=rand.nextFloat()*.6F+.4F;setRBGColorF(colour,colour,colour);}
 public int getFXLayer(){return 3;}public int getBrightnessForRender(float partial){return 61680;}
 public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(++particleAge>=particleMaxAge)setDead();}
 public void renderParticle(net.minecraft.client.renderer.Tessellator buffer,float partial,float x,float z,float yz,float xy,float xz){
  int frame=(int)((particleAge+partial)*3/4);if(frame>7)return;
  float u0=frame%4/4F,u1=u0+.24975F,v0=frame/2/2F,v1=v0+.4995F;
  float px=(float)(prevPosX+(posX-prevPosX)*partial-interpPosX),py=(float)(prevPosY+(posY-prevPosY)*partial-interpPosY),pz=(float)(prevPosZ+(posZ-prevPosZ)*partial-interpPosZ);
  int texture=org.lwjgl.opengl.GL11.glGetInteger(org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D);
  org.lwjgl.opengl.GL11.glPushAttrib(org.lwjgl.opengl.GL11.GL_ENABLE_BIT|org.lwjgl.opengl.GL11.GL_CURRENT_BIT);
  try{net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_LIGHTING);org.lwjgl.opengl.GL11.glColor4f(1,1,1,1);
  buffer.startDrawingQuads();buffer.setBrightness(61680);buffer.setColorRGBA_F(particleRed,particleGreen,particleBlue,1);buffer.setNormal(0,1,0);
  buffer.addVertexWithUV(px-x-xy,py-z*.5F,pz-yz-xz,u1,v1);buffer.addVertexWithUV(px-x+xy,py+z*.5F,pz-yz+xz,u1,v0);buffer.addVertexWithUV(px+x+xy,py+z*.5F,pz+yz+xz,u0,v0);buffer.addVertexWithUV(px+x-xy,py-z*.5F,pz+yz-xz,u0,v1);buffer.draw();
  }finally{org.lwjgl.opengl.GL11.glPopAttrib();org.lwjgl.opengl.GL11.glBindTexture(3553,texture);}
 }
}
