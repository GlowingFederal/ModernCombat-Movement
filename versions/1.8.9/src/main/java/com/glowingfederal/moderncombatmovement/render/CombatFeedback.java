package com.glowingfederal.moderncombatmovement.render;
public final class CombatFeedback {
 private CombatFeedback(){}
 public static void apply(com.glowingfederal.moderncombatmovement.network.Packets.Feedback event){
  net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.getMinecraft();net.minecraft.world.World world=mc.theWorld;
  if(event.kind==0){mc.thePlayer.motionX+=event.vx;mc.thePlayer.motionY+=event.vy;mc.thePlayer.motionZ+=event.vz;mc.effectRenderer.addEffect(new GustEmitter(world,event.x,event.y,event.z,event.radius));}
  else if(event.kind==2){mc.effectRenderer.addEffect(new SweepParticle(world,event.x,event.y,event.z));}
  else if(event.kind==1){net.minecraft.block.state.IBlockState state=world.getBlockState(new net.minecraft.util.BlockPos(event.x,event.y-.1,event.z));for(int i=0;i<64;i++)world.spawnParticle(net.minecraft.util.EnumParticleTypes.BLOCK_DUST,event.x+(world.rand.nextDouble()-.5)*3,event.y+.1,event.z+(world.rand.nextDouble()-.5)*3,(world.rand.nextDouble()-.5)*.2,.1,(world.rand.nextDouble()-.5)*.2,net.minecraft.block.Block.getStateId(state));}
 }
}
