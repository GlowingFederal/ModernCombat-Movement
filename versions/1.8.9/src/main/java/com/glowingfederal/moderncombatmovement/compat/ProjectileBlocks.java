package com.glowingfederal.moderncombatmovement.compat;
/** Native projectile-hit semantics for exact modern block identities, using their existing drops and tile lifecycle. */
public final class ProjectileBlocks {
 private ProjectileBlocks(){}
 public static boolean hit(net.minecraft.world.World world,net.minecraft.entity.Entity owner,int x,int y,int z){
  net.minecraft.util.BlockPos pos=new net.minecraft.util.BlockPos(x,y,z);net.minecraft.block.state.IBlockState state=world.getBlockState(pos);net.minecraft.block.Block block=state.getBlock();String name=String.valueOf(net.minecraft.block.Block.blockRegistry.getNameForObject(block));
  boolean pot=name.equals("etfuturum:decorated_pot"),flower=name.equals("etfuturum:chorus_flower")||name.equals("minecraft:chorus_flower");if(!pot&&!flower)return false;
  if(world.isRemote)return true;
  if(world.getGameRules().hasRule("projectilesCanBreakBlocks")&&!world.getGameRules().getBoolean("projectilesCanBreakBlocks")||world.getGameRules().hasRule("projectiles_can_break_blocks")&&!world.getGameRules().getBoolean("projectiles_can_break_blocks"))return true;
  if(owner instanceof net.minecraft.entity.player.EntityPlayer){if(!world.isBlockModifiable((net.minecraft.entity.player.EntityPlayer)owner,pos))return true;}
  else if(owner!=null&&!world.getGameRules().getBoolean("mobGriefing"))return true;
  if(pot){world.setBlockState(pos,block.getStateFromMeta(block.getMetaFromState(state)|4),2);}world.destroyBlock(pos,true);return true;
 }
}
