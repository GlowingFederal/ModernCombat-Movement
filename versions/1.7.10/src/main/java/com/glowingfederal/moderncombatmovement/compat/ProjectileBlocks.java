package com.glowingfederal.moderncombatmovement.compat;
/** Native projectile-hit semantics for exact modern block identities, using their existing drops and tile lifecycle. */
public final class ProjectileBlocks {
 private ProjectileBlocks(){}
 public static boolean hit(net.minecraft.world.World world,net.minecraft.entity.Entity owner,int x,int y,int z){
  net.minecraft.block.Block block=world.getBlock(x,y,z);String name=String.valueOf(net.minecraft.block.Block.blockRegistry.getNameForObject(block));
  boolean pot=name.equals("etfuturum:decorated_pot"),flower=name.equals("etfuturum:chorus_flower")||name.equals("minecraft:chorus_flower");if(!pot&&!flower)return false;
  if(world.isRemote)return true;
  if(world.getGameRules().hasRule("projectilesCanBreakBlocks")&&!world.getGameRules().getGameRuleBooleanValue("projectilesCanBreakBlocks")||world.getGameRules().hasRule("projectiles_can_break_blocks")&&!world.getGameRules().getGameRuleBooleanValue("projectiles_can_break_blocks"))return true;
  if(owner instanceof net.minecraft.entity.player.EntityPlayer){if(!world.canMineBlock((net.minecraft.entity.player.EntityPlayer)owner,x,y,z))return true;}
  else if(owner!=null&&!world.getGameRules().getGameRuleBooleanValue("mobGriefing"))return true;
  if(pot){world.setBlockMetadataWithNotify(x,y,z,world.getBlockMetadata(x,y,z)|4,2);}world.func_147480_a(x,y,z,true);return true;
 }
}
