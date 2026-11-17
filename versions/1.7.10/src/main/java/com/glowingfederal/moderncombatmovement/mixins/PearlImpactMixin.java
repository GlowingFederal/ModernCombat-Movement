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
 @Unique private boolean mcm$native(){return ((Object)this).getClass()==EntityEnderPearl.class&&Settings.projectiles(worldObj.isRemote);}
 @Inject(method={"onImpact","func_70184_a"},at=@At("HEAD"),cancellable=true)private void mcm$owner(net.minecraft.util.MovingObjectPosition hit,CallbackInfo ci){EntityLivingBase owner=getThrower();if(mcm$native()&&!worldObj.isRemote&&owner!=null&&(!owner.isEntityAlive()||owner.isPlayerSleeping())){setDead();ci.cancel();}}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;setPositionAndUpdate(DDD)V"),require=0)
 private void mcm$arrive(EntityLivingBase owner,double x,double y,double z){double ox=owner.posX,oy=owner.posY,oz=owner.posZ;owner.setPositionAndUpdate(x,y,z);if(mcm$native())PearlTeleport.arrived(owner,ox,oy,oz);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70634_a(DDD)V"),require=0)
 private void mcm$arriveSrg(EntityLivingBase owner,double x,double y,double z){mcm$arrive(owner,x,y,z);}
 @Redirect(method={"onImpact","func_70184_a"},at=@At(value="INVOKE",target="Lcpw/mods/fml/common/eventhandler/EventBus;post(Lcpw/mods/fml/common/eventhandler/Event;)Z"))
 private boolean mcm$event(cpw.mods.fml.common.eventhandler.EventBus bus,cpw.mods.fml.common.eventhandler.Event event){return mcm$native()?PearlTeleport.post(bus,event):bus.post(event);}
 @Inject(method={"onImpact","func_70184_a"},at=@At("RETURN"))private void mcm$nonPlayer(net.minecraft.util.MovingObjectPosition hit,CallbackInfo ci){
  EntityLivingBase owner=getThrower();if(!mcm$native()||worldObj.isRemote||owner==null||owner instanceof EntityPlayerMP||!owner.isEntityAlive()||owner.worldObj!=worldObj||owner.isPlayerSleeping())return;
  net.minecraftforge.event.entity.living.EnderTeleportEvent event=new net.minecraftforge.event.entity.living.EnderTeleportEvent(owner,posX,posY,posZ,0);
  if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))return;
  double ox=owner.posX,oy=owner.posY,oz=owner.posZ;if(owner.isRiding())owner.mountEntity(null);owner.setPositionAndUpdate(event.targetX,event.targetY,event.targetZ);PearlTeleport.arrived(owner,ox,oy,oz);
 }
}
