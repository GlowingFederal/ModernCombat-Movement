package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.*;
/** The native ground projectile remains authoritative; unrelated subclasses retain their implementation. */
@Mixin(value=EntityFireworkRocket.class,remap=false)
public abstract class GroundRocketMixin extends Entity implements GroundRocketAccess {
 @Shadow(aliases="field_184566_a") @Final private static net.minecraft.network.datasync.DataParameter<ItemStack> FIREWORK_ITEM;
 public ItemStack mcm$rocketItem(){return getDataManager().get(FIREWORK_ITEM);}
 protected GroundRocketMixin(World world){super(world);}
 @Unique private boolean mcm$collision;
 @Inject(method="<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",at=@At("RETURN"))private void mcm$components(World world,double x,double y,double z,ItemStack stack,CallbackInfo ci){EntityFireworkRocket rocket=(EntityFireworkRocket)(Object)this;GroundRockets.initialize(rocket,stack);if(GroundRockets.owns(rocket)){motionX=(rand.nextDouble()-rand.nextDouble())*.002297;motionZ=(rand.nextDouble()-rand.nextDouble())*.002297;}}
 @ModifyConstant(method={"onUpdate","func_70071_h_"},constant=@Constant(doubleValue=1.15))private double mcm$horizontalDrag(double original){return GroundRockets.owns((EntityFireworkRocket)(Object)this)&&collidedHorizontally?1:original;}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD"))private void mcm$segment(CallbackInfo ci){EntityFireworkRocket rocket=(EntityFireworkRocket)(Object)this;mcm$collision=false;if(!world.isRemote&&GroundRockets.owns(rocket)){double factor=collidedHorizontally?1:1.15;mcm$collision=RocketEffects.hits(rocket,GroundRockets.owner(rocket),motionX*factor,motionY+.04,motionZ*factor,RocketEffects.stars(GroundRockets.item(rocket)));}}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("RETURN"))private void mcm$collision(CallbackInfo ci){if(mcm$collision&&!isDead&&GroundRockets.owns((EntityFireworkRocket)(Object)this))GroundRockets.explode((EntityFireworkRocket)(Object)this);}
 @Inject(method={"dealExplosionDamage","func_191510_k"},at=@At("HEAD"),cancellable=true)private void mcm$groundDamage(CallbackInfo ci){EntityFireworkRocket rocket=(EntityFireworkRocket)(Object)this;if(GroundRockets.owns(rocket)){GroundRockets.damage(rocket);ci.cancel();}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;move(Lnet/minecraft/entity/MoverType;DDD)V"),require=0,expect=0)
 private void mcm$moveEntityMcp(Entity entity,net.minecraft.entity.MoverType type,double x,double y,double z){entity.move(type,x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70091_d(Lnet/minecraft/entity/MoverType;DDD)V"),require=0,expect=0)
 private void mcm$moveEntitySrg(Entity entity,net.minecraft.entity.MoverType type,double x,double y,double z){entity.move(type,x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/item/EntityFireworkRocket;move(Lnet/minecraft/entity/MoverType;DDD)V"),require=0,expect=0)
 private void mcm$moveRocketMcp(EntityFireworkRocket entity,net.minecraft.entity.MoverType type,double x,double y,double z){entity.move(type,x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/item/EntityFireworkRocket;func_70091_d(Lnet/minecraft/entity/MoverType;DDD)V"),require=0,expect=0)
 private void mcm$moveRocketSrg(EntityFireworkRocket entity,net.minecraft.entity.MoverType type,double x,double y,double z){entity.move(type,x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
}
