/* Native 1.12 ParticleEmitter, adapted without a parallel particle engine. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.EntityLivingBase;
public final class TotemEmitter extends EntityFX {
 private final EntityLivingBase attached;
 private int age;
 public TotemEmitter(EntityLivingBase entity){super(entity.worldObj,entity.posX,entity.posY+entity.height/2,entity.posZ);attached=entity;onUpdate();}
 @Override public int getFXLayer(){return 3;}
 @Override public void renderParticle(net.minecraft.client.renderer.Tessellator buffer,float partial,float x,float z,float yz,float xy,float xz){}
 @Override public void onUpdate(){
  for(int i=0;i<16;i++){
   double x=rand.nextFloat()*2-1,y=rand.nextFloat()*2-1,z=rand.nextFloat()*2-1;
   if(x*x+y*y+z*z>1)continue;
   Minecraft.getMinecraft().effectRenderer.addEffect(new TotemParticle(worldObj,attached.posX+x*attached.width/4,attached.boundingBox.minY+attached.height/2+y*attached.height/4,attached.posZ+z*attached.width/4,x,y+.2,z));
  }
  if(++age>=30)setDead();
 }
}
