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
  com.glowingfederal.moderncombatmovement.render.ElytraWings.rotate(p,partial);
  if(Settings.rendering&&PlayerHooks.state(p).spinTicks>0){GL11.glRotatef(-90-p.rotationPitch,1,0,0);GL11.glRotatef((p.ticksExisted+partial)*-75,0,1,0);GL11.glTranslatef(0,-1,0);return;}
  if(!Settings.rendering || !PlayerHooks.active(p)) return;
  if(com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(p))return;
  PlayerState s=PlayerHooks.state(p); float amount=s.swim(partial);
  if(amount>0) { if(!p.isInWater()&&s.pose==Pose.LOW)GL11.glTranslatef(0,-3F/16*amount,0);GL11.glRotatef(amount*(p.isInWater()?-90-p.rotationPitch:-90),1,0,0); GL11.glTranslatef(0,-amount,amount*.3F); }
 }
 @Redirect(method={"doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V","func_76986_a(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V","renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;getItemInUseCount()I"),require=0)private int mcm$mainUseMcp(net.minecraft.client.entity.AbstractClientPlayer p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
 @Redirect(method={"doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V","func_76986_a(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V","renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;func_71052_bv()I"),require=0)private int mcm$mainUseSrg(net.minecraft.client.entity.AbstractClientPlayer p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
 @Inject(method={"renderFirstPersonArm","func_82441_a"},at=@At("HEAD"))private void mcm$armStart(net.minecraft.entity.player.EntityPlayer player,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ModelRenderContext.firstPersonArms++;}
 @Inject(method={"renderFirstPersonArm","func_82441_a"},at=@At("RETURN"))private void mcm$armEnd(net.minecraft.entity.player.EntityPlayer player,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ModelRenderContext.firstPersonArms--;}

 @Redirect(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="FIELD",target="Lnet/minecraft/client/entity/AbstractClientPlayer;fishEntity:Lnet/minecraft/entity/projectile/EntityFishHook;"),require=0)private net.minecraft.entity.projectile.EntityFishHook mcm$mainRod(net.minecraft.client.entity.AbstractClientPlayer player){return com.glowingfederal.moderncombatmovement.render.FishingHand.off(player)?null:player.fishEntity;}
 @Redirect(method={"renderEquippedItems(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V","func_77029_c(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V"},at=@At(value="FIELD",target="Lnet/minecraft/client/entity/AbstractClientPlayer;field_71104_cf:Lnet/minecraft/entity/projectile/EntityFishHook;"),require=0)private net.minecraft.entity.projectile.EntityFishHook mcm$mainRodSrg(net.minecraft.client.entity.AbstractClientPlayer player){return mcm$mainRod(player);}

}
