package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.Shields;
/** Native hurt-time bookkeeping remains authoritative after reducing a fully blocked hit to zero. */
@Mixin(value=EntityLivingBase.class,remap=false)
public abstract class ShieldDamageMixin implements com.glowingfederal.moderncombatmovement.combat.ShieldBlockAccess {
 @Unique private boolean mcm$shieldBlocked;
 @Unique private DamageSource mcm$shieldSource;
 public boolean mcm$fullyBlocked(){return mcm$shieldBlocked;}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD"))private void mcm$begin(DamageSource source,float damage,CallbackInfoReturnable<Boolean> ci){mcm$shieldBlocked=false;mcm$shieldSource=source;}
 @Group(name="mcm$shieldDamage",min=1,max=1)
 @ModifyVariable(method={"attackEntityFrom","func_70097_a"},at=@At(value="FIELD",target="Lnet/minecraft/entity/EntityLivingBase;limbSwingAmount:F",opcode=181),argsOnly=true,ordinal=0,require=0)
 private float mcm$shieldMcp(float damage){return mcm$shield(damage,mcm$shieldSource);}
 @Group(name="mcm$shieldDamage",min=1,max=1)
 @ModifyVariable(method={"attackEntityFrom","func_70097_a"},at=@At(value="FIELD",target="Lnet/minecraft/entity/EntityLivingBase;field_70721_aZ:F",opcode=181),argsOnly=true,ordinal=0,require=0)
 private float mcm$shieldSrg(float damage){return mcm$shield(damage,mcm$shieldSource);}
 @Unique private float mcm$shield(float damage,DamageSource source){
  EntityLivingBase e=(EntityLivingBase)(Object)this;
  mcm$shieldBlocked=e instanceof EntityPlayer&&Shields.block((EntityPlayer)e,source,damage);
  return mcm$shieldBlocked?0:damage;
 }
 @Redirect(method={"attackEntityFrom","func_70097_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;setEntityState(Lnet/minecraft/entity/Entity;B)V"),require=0)private void mcm$hurt(World world,Entity entity,byte state){if(!mcm$shieldBlocked||state!=2)world.setEntityState(entity,state);}
 @Redirect(method={"attackEntityFrom","func_70097_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72960_a(Lnet/minecraft/entity/Entity;B)V"),require=0)private void mcm$hurtSrg(World world,Entity entity,byte state){if(!mcm$shieldBlocked||state!=2)world.setEntityState(entity,state);}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("RETURN"),cancellable=true)private void mcm$result(DamageSource source,float damage,CallbackInfoReturnable<Boolean> ci){if(mcm$shieldBlocked){mcm$shieldBlocked=false;ci.setReturnValue(false);}}
}
