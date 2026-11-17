package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.movement.GroundMovement;
@Mixin(value=Entity.class,remap=false)
public abstract class GroundMovementMixin {
 @Unique private double mcm$edgeX,mcm$edgeZ;
 @Inject(method={"moveEntity","func_70091_d"},at=@At("HEAD"))
 private void mcm$prepareEdge(double x,double y,double z,CallbackInfo ci){
  mcm$edgeX=x;mcm$edgeZ=z;
  if((Object)this instanceof EntityPlayer ){mcm$edgeX=GroundMovement.edge((EntityPlayer)(Object)this,x,y,z,true);mcm$edgeZ=GroundMovement.edge((EntityPlayer)(Object)this,x,y,z,false);}
 }
 @Group(name="mcmEdgeX",min=1) @ModifyVariable(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",ordinal=0),argsOnly=true,ordinal=0,require=0,expect=0)
 private double mcm$edgeXMcp(double value){return mcm$edgeX;}
 @Group(name="mcmEdgeX",min=1) @ModifyVariable(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/profiler/Profiler;func_76320_a(Ljava/lang/String;)V",ordinal=0),argsOnly=true,ordinal=0,require=0,expect=0)
 private double mcm$edgeXSrg(double value){return mcm$edgeX;}
 @Group(name="mcmEdgeZ",min=1) @ModifyVariable(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",ordinal=0),argsOnly=true,ordinal=2,require=0,expect=0)
 private double mcm$edgeZMcp(double value){return mcm$edgeZ;}
 @Group(name="mcmEdgeZ",min=1) @ModifyVariable(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/profiler/Profiler;func_76320_a(Ljava/lang/String;)V",ordinal=0),argsOnly=true,ordinal=2,require=0,expect=0)
 private double mcm$edgeZSrg(double value){return mcm$edgeZ;}
 @Group(name="mcmNativeEdge",min=1) @Redirect(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;isSneaking()Z",ordinal=0),require=0,expect=0)
 private boolean mcm$nativeEdgeMcp(Entity e){return e instanceof EntityPlayer&&GroundMovement.owns((EntityPlayer)e)?false:e.isSneaking();}
 @Group(name="mcmNativeEdge",min=1) @Redirect(method={"moveEntity","func_70091_d"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;func_70093_af()Z",ordinal=0),require=0,expect=0)
 private boolean mcm$nativeEdgeSrg(Entity e){return e instanceof EntityPlayer&&GroundMovement.owns((EntityPlayer)e)?false:e.isSneaking();}
}
