package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.Shields;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class ShieldPlayerMixin {
 @Inject(method={"blockUsingShield","func_190629_c"},at=@At("HEAD"),cancellable=true)private void mcm$modernDisable(EntityLivingBase attacker,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;if(Shields.owns(p)||((com.glowingfederal.moderncombatmovement.combat.ShieldBlockAccess)p).mcm$fullyBlocked()){if(Shields.owns(p))Shields.blocked(p,attacker);ci.cancel();}}
}
