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
 @Inject(method={"applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V","func_77043_a(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V"},at=@At("RETURN")) private void mcm$lowPose(AbstractClientPlayer p,float age,float yaw,float partial,CallbackInfo ci) {
  if(Settings.rendering&&PlayerHooks.state(p).spinTicks>0){GL11.glRotatef(-90-p.rotationPitch,1,0,0);GL11.glRotatef((p.ticksExisted+partial)*-75,0,1,0);GL11.glTranslatef(0,-1,0);return;}
  if(!Settings.rendering || !PlayerHooks.active(p)) return;
  if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p))return;
  PlayerState s=PlayerHooks.state(p); float amount=s.swim(partial);
  if(amount>0) { if(!p.isInWater()&&s.pose==Pose.LOW)GL11.glTranslatef(0,-3F/16*amount,0);GL11.glRotatef(amount*(p.isInWater()?-90-p.rotationPitch:-90),1,0,0); GL11.glTranslatef(0,-amount,amount*.3F); }
 }
 @Inject(method={"renderRightArm","func_177138_b","renderLeftArm","func_177139_c"},at=@At("HEAD"))private void mcm$armStart(net.minecraft.client.entity.AbstractClientPlayer player,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ModelRenderContext.firstPersonArms++;}
 @Inject(method={"renderRightArm","func_177138_b","renderLeftArm","func_177139_c"},at=@At("RETURN"))private void mcm$armEnd(net.minecraft.client.entity.AbstractClientPlayer player,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ModelRenderContext.firstPersonArms--;}

}
