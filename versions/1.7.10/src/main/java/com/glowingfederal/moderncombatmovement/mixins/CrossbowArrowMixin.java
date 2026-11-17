package com.glowingfederal.moderncombatmovement.mixins;

import java.util.List;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.compat.CrossbowBolt;
@Mixin(value=EntityArrow.class,remap=false)
public abstract class CrossbowArrowMixin {
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"),require=0) private boolean mcm$hitMcp(Entity target,DamageSource source,float damage) {
  CrossbowBolt bolt=(Object)this instanceof CrossbowBolt?(CrossbowBolt)(Object)this:null;
  if(bolt!=null && !bolt.mayHit(target)){bolt.discard();return false;}
  boolean success=target.attackEntityFrom(source,damage);if(bolt!=null)bolt.record(target,success);return success;
 }
@Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70097_a(Lnet/minecraft/util/DamageSource;F)Z"),require=0) private boolean mcm$hitSrg(Entity target,DamageSource source,float damage) {
  CrossbowBolt bolt=(Object)this instanceof CrossbowBolt?(CrossbowBolt)(Object)this:null;
  if(bolt!=null && !bolt.mayHit(target)){bolt.discard();return false;}
  boolean success=target.attackEntityFrom(source,damage);if(bolt!=null)bolt.record(target,success);return success;
 }

 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"),require=0) private List<Entity> mcm$filterMcp(World world,Entity excluded,net.minecraft.util.AxisAlignedBB box) {
  List<Entity> entities=world.getEntitiesWithinAABBExcludingEntity(excluded,box);return (Object)this instanceof CrossbowBolt ? ((CrossbowBolt)(Object)this).filter(entities):entities;
 }
@Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72839_b(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;"),require=0) private List<Entity> mcm$filterSrg(World world,Entity excluded,net.minecraft.util.AxisAlignedBB box) {
  List<Entity> entities=world.getEntitiesWithinAABBExcludingEntity(excluded,box);return (Object)this instanceof CrossbowBolt ? ((CrossbowBolt)(Object)this).filter(entities):entities;
 }

 @ModifyConstant(method={"onUpdate","func_70071_h_"},constant=@Constant(floatValue=.8F),require=0) private float mcm$waterDrag(float nativeValue) { return (Object)this instanceof CrossbowBolt ? .6F : nativeValue; }
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"),require=0)
 private void mcm$block(net.minecraft.block.Block block,World world,int x,int y,int z,Entity arrow){if(!(arrow instanceof CrossbowBolt)||!com.glowingfederal.moderncombatmovement.compat.ProjectileBlocks.hit(world,((EntityArrow)arrow).shootingEntity,x,y,z))block.onEntityCollidedWithBlock(world,x,y,z,arrow);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/block/Block;func_149670_a(Lnet/minecraft/world/World;IIILnet/minecraft/entity/Entity;)V"),require=0)
 private void mcm$blockSrg(net.minecraft.block.Block block,World world,int x,int y,int z,Entity arrow){mcm$block(block,world,x,y,z,arrow);}

}
