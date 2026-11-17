package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Native 1.12 delivery remains authoritative; the selected modern entity-hit origin is scoped to vanilla bottles. */
@Mixin(value=EntityPotion.class,remap=false)
public abstract class LingeringPotionMixin {
 @Unique private Entity mcm$impactTarget;
 @Inject(method={"onImpact","func_70184_a"},at=@At("HEAD"))
 private void mcm$impact(RayTraceResult hit,CallbackInfo ci){mcm$impactTarget=hit.entityHit;}
 @Inject(method={"onImpact","func_70184_a"},at=@At("RETURN"))
 private void mcm$done(RayTraceResult hit,CallbackInfo ci){mcm$impactTarget=null;}
 @Redirect(method={"makeAreaOfEffectCloud","func_190542_a"},at=@At(value="NEW",target="net/minecraft/entity/EntityAreaEffectCloud"))
 private EntityAreaEffectCloud mcm$cloud(World world,double x,double y,double z){
  boolean managed=((Object)this).getClass()==EntityPotion.class&&com.glowingfederal.moderncombatmovement.config.Settings.kit(world.isRemote);
  EntityAreaEffectCloud cloud=managed&&mcm$impactTarget!=null?new EntityAreaEffectCloud(world,mcm$impactTarget.posX,mcm$impactTarget.posY,mcm$impactTarget.posZ):new EntityAreaEffectCloud(world,x,y,z);
  if(managed)cloud.getEntityData().setBoolean("MCMThrownPotion",true);return cloud;
 }
}
