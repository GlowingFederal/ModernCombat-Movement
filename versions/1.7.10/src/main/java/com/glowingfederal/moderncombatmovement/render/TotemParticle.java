/* Native 1.12 ParticleTotem and ParticleSimpleAnimated, using the legacy batched block atlas. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
public final class TotemParticle extends EntityFX {
 private static final net.minecraft.util.IIcon[] FRAMES=new net.minecraft.util.IIcon[8];
 static void stitched(){for(int i=0;i<8;i++)FRAMES[i]=new Frame(TotemActivation.atlas,i);}
 public TotemParticle(World world,double x,double y,double z,double vx,double vy,double vz){
  super(world,x,y,z);motionX=vx;motionY=vy;motionZ=vz;particleScale*=.75F;particleMaxAge=60+rand.nextInt(12);
  if(rand.nextInt(4)==0)setRBGColorF(.6F+rand.nextFloat()*.2F,.6F+rand.nextFloat()*.3F,rand.nextFloat()*.2F);
  else setRBGColorF(.1F+rand.nextFloat()*.2F,.4F+rand.nextFloat()*.3F,rand.nextFloat()*.2F);
  frame(7);
 }
 @Override public int getFXLayer(){return 1;}
 @Override public int getBrightnessForRender(float partial){return 15728880;}
 @Override public void onUpdate(){
  prevPosX=posX;prevPosY=posY;prevPosZ=posZ;
  if(particleAge++>=particleMaxAge){setDead();return;}
  if(particleAge>particleMaxAge/2)particleAlpha=1-((float)particleAge-particleMaxAge/2)/particleMaxAge;
  frame(Math.max(0,7-particleAge*8/particleMaxAge));motionY-=.05F;moveEntity(motionX,motionY,motionZ);motionX*=.6F;motionY*=.6F;motionZ*=.6F;
  if(onGround){motionX*=.7F;motionZ*=.7F;}
 }
 private void frame(int frame){if(FRAMES[frame]!=null)setParticleIcon(FRAMES[frame]);}
 private static final class Frame implements net.minecraft.util.IIcon {
  private final net.minecraft.util.IIcon atlas;private final int column;
  Frame(net.minecraft.util.IIcon atlas,int column){this.atlas=atlas;this.column=column;}
  public int getIconWidth(){return atlas.getIconWidth()/16;}public int getIconHeight(){return atlas.getIconHeight()/16;}
  public float getMinU(){return atlas.getInterpolatedU(column);}public float getMaxU(){return atlas.getInterpolatedU(column+1);}
  public float getMinV(){return atlas.getInterpolatedV(11);}public float getMaxV(){return atlas.getInterpolatedV(12);}
  public float getInterpolatedU(double u){return atlas.getInterpolatedU(column+u/16);}public float getInterpolatedV(double v){return atlas.getInterpolatedV(11+v/16);}
  public String getIconName(){return atlas.getIconName();}
 }
}
