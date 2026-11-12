package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=RenderPlayer.class,remap=false)
public abstract class RenderPlayerMixin {
 @Inject(method={"rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V","func_77043_a(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V"},at=@At("RETURN")) private void mcm$lowPose(AbstractClientPlayer p,float age,float yaw,float partial,CallbackInfo ci) {
  if(!Settings.rendering || !PlayerHooks.active(p)) return;
  PlayerState s=PlayerHooks.state(p); float amount=s.swim(partial);
  if(amount>0) { GL11.glRotatef(amount*(-90-p.rotationPitch),1,0,0); GL11.glTranslatef(0,-amount,amount*.3F); }
 }
}
