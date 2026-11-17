package com.glowingfederal.moderncombatmovement.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** Resolve the display option at the native HUD boundary without changing or saving Video Settings. */
@Mixin(value=net.minecraft.client.gui.GuiIngame.class,remap=false)
public abstract class AttackIndicatorMixin {
 @Redirect(method={"renderAttackIndicator","func_184045_a","renderHotbar","func_180479_a"},at=@At(value="FIELD",target="Lnet/minecraft/client/settings/GameSettings;attackIndicator:I"),require=0)
 private int mcm$display(net.minecraft.client.settings.GameSettings settings){String mode=com.glowingfederal.moderncombatmovement.config.ClientOptions.attackIndicator;return mode.equals("VANILLA")?settings.attackIndicator:mode.equals("OFF")?0:mode.equals("HOTBAR")?2:1;}
 @Redirect(method={"renderAttackIndicator","func_184045_a","renderHotbar","func_180479_a"},at=@At(value="FIELD",target="Lnet/minecraft/client/settings/GameSettings;field_186716_M:I"),require=0)
 private int mcm$displaySrg(net.minecraft.client.settings.GameSettings settings){return mcm$display(settings);}
}
