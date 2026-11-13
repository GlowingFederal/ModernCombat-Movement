package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
/** Correct only native attached rockets; foreign projectile subclasses retain their implementation. */
@Mixin(value=EntityFireworkRocket.class,remap=false)
public abstract class FireworkFlightMixin {
 private Vec3d mcm$direction(EntityLivingBase owner){if(((Object)this).getClass()==EntityFireworkRocket.class&&owner instanceof EntityPlayer&&FlightHooks.managed((EntityPlayer)owner))return FlightHooks.look((EntityPlayer)owner);return owner.getLookVec();}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;getLookVec()Lnet/minecraft/util/math/Vec3d;"),require=0)private Vec3d mcm$lookMcp(EntityLivingBase owner){return mcm$direction(owner);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/EntityLivingBase;func_70040_Z()Lnet/minecraft/util/math/Vec3d;"),require=0)private Vec3d mcm$lookSrg(EntityLivingBase owner){return mcm$direction(owner);}
}
