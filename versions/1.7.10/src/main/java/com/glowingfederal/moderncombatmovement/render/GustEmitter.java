/* Native GustSeedParticle emitter, scheduled by the existing particle manager. */
package com.glowingfederal.moderncombatmovement.render;
public final class GustEmitter extends net.minecraft.client.particle.EntityFX {
 private final double spread;private final int duration,delay;
 public GustEmitter(net.minecraft.world.World world,double x,double y,double z,float radius){super(world,x,y,z);motionX=motionY=motionZ=0;boolean small=radius<2;spread=small?1:3;duration=small?3:7;delay=small?2:0;}
 public int getFXLayer(){return 3;}public void renderParticle(net.minecraft.client.renderer.Tessellator buffer,float partial,float x,float z,float yz,float xy,float xz){}
 public void onUpdate(){if(particleAge%(delay+1)==0)for(int i=0;i<3;i++)net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(new GustParticle(worldObj,posX+(rand.nextDouble()-rand.nextDouble())*spread,posY+(rand.nextDouble()-rand.nextDouble())*spread,posZ+(rand.nextDouble()-rand.nextDouble())*spread,false));if(particleAge++==duration)setDead();}
}
