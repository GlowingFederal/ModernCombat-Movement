package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.Shields;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class ShieldPlayerMixin {
 @Inject(method={"isBlocking","func_71039_bw"},at=@At("HEAD"),cancellable=true)private void mcm$noSwordReduction(CallbackInfoReturnable<Boolean> ci){EntityPlayer p=(EntityPlayer)(Object)this;ItemStack stack=((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack();if(Shields.known(stack)&&Shields.enabled(p))ci.setReturnValue(false);}
}
