/* Native GustParticle sprite animation and providers, adapted to the legacy block atlas. */
package com.glowingfederal.moderncombatmovement.render;
public final class GustParticle extends net.minecraft.client.particle.EntityFX {
 private static final net.minecraft.util.IIcon[] LARGE=new net.minecraft.util.IIcon[12],SMALL=new net.minecraft.util.IIcon[7];private final net.minecraft.util.IIcon[] sprites;
 public GustParticle(net.minecraft.world.World world,double x,double y,double z,boolean small){super(world,x,y,z);sprites=small?SMALL:LARGE;motionX=motionY=motionZ=0;particleMaxAge=12+rand.nextInt(4);particleScale=small?1.5F:10;setParticleIcon(sprites[0]);}
 public int getFXLayer(){return 1;}public int getBrightnessForRender(float partial){return 15728880;}
 public void onUpdate(){prevPosX=posX;prevPosY=posY;prevPosZ=posZ;if(particleAge++>=particleMaxAge){setDead();return;}setParticleIcon(sprites[Math.min(sprites.length-1,particleAge*sprites.length/particleMaxAge)]);}
 public static final class Textures {
  @cpw.mods.fml.common.eventhandler.SubscribeEvent public void stitch(net.minecraftforge.client.event.TextureStitchEvent.Pre e){if(e.map.getTextureType()!=0)return;for(int i=0;i<12;i++){String name="gust_"+i;LARGE[i]=e.map.registerIcon("moderncombatmovement:feedback/"+name);}for(int i=0;i<7;i++){String name="small_gust_"+i;SMALL[i]=e.map.registerIcon("moderncombatmovement:feedback/"+name);}}
 }
}
