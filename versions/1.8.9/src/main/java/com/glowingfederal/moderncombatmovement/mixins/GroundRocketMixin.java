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
public abstract class GroundRocketMixin extends Entity {
 protected GroundRocketMixin(World world){super(world);}
 @Unique private boolean mcm$collision;
 @Inject(method="<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",at=@At("RETURN"))private void mcm$components(World world,double x,double y,double z,ItemStack stack,CallbackInfo ci){EntityFireworkRocket rocket=(EntityFireworkRocket)(Object)this;GroundRockets.initialize(rocket,stack);if(GroundRockets.owns(rocket)){motionX=(rand.nextDouble()-rand.nextDouble())*.002297;motionZ=(rand.nextDouble()-rand.nextDouble())*.002297;}}
 @ModifyConstant(method={"onUpdate","func_70071_h_"},constant=@Constant(doubleValue=1.15))private double mcm$horizontalDrag(double original){return GroundRockets.owns((EntityFireworkRocket)(Object)this)&&isCollidedHorizontally?1:original;}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD"))private void mcm$segment(CallbackInfo ci){EntityFireworkRocket rocket=(EntityFireworkRocket)(Object)this;mcm$collision=false;if(!worldObj.isRemote&&GroundRockets.owns(rocket)){double factor=isCollidedHorizontally?1:1.15;mcm$collision=RocketEffects.hits(rocket,GroundRockets.owner(rocket),motionX*factor,motionY+.04,motionZ*factor,RocketEffects.stars(GroundRockets.item(rocket)));}}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("RETURN"))private void mcm$collision(CallbackInfo ci){if(mcm$collision&&!isDead&&GroundRockets.owns((EntityFireworkRocket)(Object)this))GroundRockets.explode((EntityFireworkRocket)(Object)this);}
 @Group(name="mcmGroundRocketExpiry",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;setEntityState(Lnet/minecraft/entity/Entity;B)V"),require=0,expect=0)private void mcm$expiredMcp(World world,Entity entity,byte state){if(state==17&&entity==(Object)this&&GroundRockets.owns((EntityFireworkRocket)(Object)this))GroundRockets.damage((EntityFireworkRocket)(Object)this);world.setEntityState(entity,state);}
@Group(name="mcmGroundRocketExpiry",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72960_a(Lnet/minecraft/entity/Entity;B)V"),require=0,expect=0)private void mcm$expiredSrg(World world,Entity entity,byte state){if(state==17&&entity==(Object)this&&GroundRockets.owns((EntityFireworkRocket)(Object)this))GroundRockets.damage((EntityFireworkRocket)(Object)this);world.setEntityState(entity,state);}

 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;moveEntity(DDD)V"),require=0,expect=0)
 private void mcm$moveEntityMcp(Entity entity,double x,double y,double z){entity.moveEntity(x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70091_d(DDD)V"),require=0,expect=0)
 private void mcm$moveEntitySrg(Entity entity,double x,double y,double z){entity.moveEntity(x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/item/EntityFireworkRocket;moveEntity(DDD)V"),require=0,expect=0)
 private void mcm$moveRocketMcp(EntityFireworkRocket entity,double x,double y,double z){entity.moveEntity(x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
 @Group(name="mcmGroundRocketMovement",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/item/EntityFireworkRocket;func_70091_d(DDD)V"),require=0,expect=0)
 private void mcm$moveRocketSrg(EntityFireworkRocket entity,double x,double y,double z){entity.moveEntity(x,y,z);if(GroundRockets.owns((EntityFireworkRocket)(Object)this)){motionX=x;motionY=y;motionZ=z;}}
}
