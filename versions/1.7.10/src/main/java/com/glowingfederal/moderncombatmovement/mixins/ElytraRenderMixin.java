package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
@Mixin(value=RenderPlayer.class,remap=false)
public abstract class ElytraRenderMixin {
 @Inject(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At("RETURN"))private void mcm$wings(AbstractClientPlayer p,float partial,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ElytraWings.render(p,partial,.0625F);}
 @Redirect(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;getHideCape()Z"),require=0)private boolean mcm$capeMcp(AbstractClientPlayer p){return p.getHideCape()||FlightHooks.managed(p);}
 @Redirect(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;func_82238_cc()Z"),require=0)private boolean mcm$capeSrg(AbstractClientPlayer p){return p.getHideCape()||FlightHooks.managed(p);}
}
