/* Scoped native pearl callbacks; native collision, particle, event and damage paths remain authoritative. */
package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import com.glowingfederal.moderncombatmovement.compat.PearlTeleport;
import com.glowingfederal.moderncombatmovement.config.Settings;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=EntityEnderPearl.class,remap=false)
public abstract class PearlImpactMixin extends EntityThrowable {
 protected PearlImpactMixin(World world){super(world);}
 @Unique private boolean mcm$native(){return ((Object)this).getClass()==EntityEnderPearl.class&&Settings.projectiles(world.isRemote);}
 @Inject(method={"onImpact","func_70184_a"},at=@At("HEAD"),cancellable=true)private void mcm$owner(net.minecraft.util.math.RayTraceResult hit,CallbackInfo ci){EntityLivingBase owner=getThrower();if(mcm$native()&&!world.isRemote&&owner!=null&&(!owner.isEntityAlive()||owner.isPlayerSleeping())){setDead();ci.cancel();}}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;setPositionAndUpdate(DDD)V"),require=0)
 private void mcm$arrive(EntityLivingBase owner,double x,double y,double z){double ox=owner.posX,oy=owner.posY,oz=owner.posZ;owner.setPositionAndUpdate(x,y,z);if(mcm$native())PearlTeleport.arrived(owner,ox,oy,oz);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70634_a(DDD)V"),require=0)
 private void mcm$arriveSrg(EntityLivingBase owner,double x,double y,double z){mcm$arrive(owner,x,y,z);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z"))
 private boolean mcm$event(net.minecraftforge.fml.common.eventhandler.EventBus bus,net.minecraftforge.fml.common.eventhandler.Event event){return mcm$native()?PearlTeleport.post(bus,event):bus.post(event);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$spawn(World world,Entity entity){if(mcm$native()&&entity instanceof net.minecraft.entity.monster.EntityEndermite&&(!com.glowingfederal.moderncombatmovement.compat.WorldSpawnRules.hostile(world)||world.getDifficulty()==net.minecraft.world.EnumDifficulty.PEACEFUL))return false;return world.spawnEntity(entity);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72838_d(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$spawnSrg(World world,Entity entity){return mcm$spawn(world,entity);}

}
