package com.glowingfederal.moderncombatmovement.mixins.uab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.ModEnchantments",remap=false)
public abstract class MendingUabMixin {
 @Inject(method="onPlayerPickupXP",at=@At("HEAD"),cancellable=true)private static void mcm$repairOwner(PlayerPickupXpEvent event,CallbackInfo ci){if(com.glowingfederal.moderncombatmovement.combat.RepairEnchantments.enabled(event.entityPlayer))ci.cancel();}
}
