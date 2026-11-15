package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=RenderPlayer.class,remap=false)
public abstract class OffhandRenderMixin {
 @Inject(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At("RETURN"))private void mcm$held(AbstractClientPlayer p,float partial,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.OffhandClient.held(((RenderPlayer)(Object)this).modelBipedMain,p);}
}
