package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.*;
/** UAB dispenser/API projectiles and already saved projectiles use the same modern cloud owner. */
@Pseudo @Mixin(targets="ganymedes01.etfuturum.entities.EntityLingeringPotion",remap=false)
public abstract class LingeringProjectileUabMixin extends EntityPotion {
 public LingeringProjectileUabMixin(World world){super(world);}
 @Shadow public abstract ItemStack getStack();
 @Inject(method={"onImpact","func_70184_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$cloud(MovingObjectPosition hit,CallbackInfo ci){
  if(!com.glowingfederal.moderncombatmovement.config.Settings.kit(worldObj.isRemote)||!PotionDelivery.enabled())return;
  if(!worldObj.isRemote){ItemStack stack=getStack();if(stack!=null){LingeringCloud cloud=new LingeringCloud(worldObj,stack,getThrower());cloud.setPosition(hit.entityHit==null?posX:hit.entityHit.posX,hit.entityHit==null?posY:hit.entityHit.posY,hit.entityHit==null?posZ:hit.entityHit.posZ);worldObj.spawnEntityInWorld(cloud);worldObj.playAuxSFX(2002,(int)Math.floor(posX),(int)Math.floor(posY),(int)Math.floor(posZ),stack.getItemDamage());}setDead();}ci.cancel();
 }
}
