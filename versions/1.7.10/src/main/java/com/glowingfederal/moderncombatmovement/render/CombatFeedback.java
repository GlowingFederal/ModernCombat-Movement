package com.glowingfederal.moderncombatmovement.render;
public final class CombatFeedback {
 private CombatFeedback(){}
 public static void apply(com.glowingfederal.moderncombatmovement.network.Packets.Feedback event){
  net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.getMinecraft();net.minecraft.world.World world=mc.theWorld;
  if(event.kind==0){mc.thePlayer.motionX+=event.vx;mc.thePlayer.motionY+=event.vy;mc.thePlayer.motionZ+=event.vz;mc.effectRenderer.addEffect(new GustEmitter(world,event.x,event.y,event.z,event.radius));}
  else if(event.kind==2){mc.effectRenderer.addEffect(new SweepParticle(world,event.x,event.y,event.z));}
  else if(event.kind==1){int x=(int)Math.floor(event.x),y=(int)Math.floor(event.y)-1,z=(int)Math.floor(event.z);net.minecraft.block.Block block=world.getBlock(x,y,z);int meta=world.getBlockMetadata(x,y,z);for(int i=0;i<64;i++)world.spawnParticle("blockdust_"+net.minecraft.block.Block.getIdFromBlock(block)+"_"+meta,event.x+(world.rand.nextDouble()-.5)*3,event.y+.1,event.z+(world.rand.nextDouble()-.5)*3,(world.rand.nextDouble()-.5)*.2,.1,(world.rand.nextDouble()-.5)*.2);}
 }
}
