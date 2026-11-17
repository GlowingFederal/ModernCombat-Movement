package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.ToolInteractions;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.Gameplay",remap=false)
public abstract class ToolUabMixin {
 @Inject(method="strip",at=@At("HEAD"),cancellable=true)private void mcm$toolPriority(PlayerInteractEvent.RightClickBlock event,CallbackInfo ci){if(event.getHand()==EnumHand.MAIN_HAND&&ToolInteractions.yields(event.getEntityPlayer(),event.getItemStack()))ci.cancel();}
}
