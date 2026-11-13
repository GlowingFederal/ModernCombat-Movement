package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Keep Forge's relative eye-height getter contract while giving picking an absolute pose-eye origin. */
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class PlayerEyeClientMixin {
 @Inject(method={"getPosition","func_70666_h"},at=@At("HEAD"),cancellable=true)
 private void mcm$position(float partial,CallbackInfoReturnable<Vec3> ci) {
  EntityPlayer p=(EntityPlayer)(Object)this;if(!PlayerHooks.active(p))return;
  double x=p.prevPosX+(p.posX-p.prevPosX)*partial,z=p.prevPosZ+(p.posZ-p.prevPosZ)*partial;
  double feet=p.prevPosY+(p.posY-p.prevPosY)*partial+p.boundingBox.minY-p.posY;
  ci.setReturnValue(Vec3.createVectorHelper(x,feet+PlayerHooks.state(p).pose.eyeHeight,z));
 }
}
