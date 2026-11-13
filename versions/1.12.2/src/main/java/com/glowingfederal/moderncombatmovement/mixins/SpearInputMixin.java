package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.combat.*;
import com.glowingfederal.moderncombatmovement.network.Packets;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
/** The modern piercing attack is an action, including air swings; vanilla entity-attack packets do not choose its victims. */
@Mixin(value=Minecraft.class,remap=false)
public abstract class SpearInputMixin {
 @Inject(method={"clickMouse","func_147116_af"},at=@At("HEAD"),cancellable=true)private void mcm$jab(CallbackInfo ci){
  EntityPlayer p=Minecraft.getMinecraft().player;if(p==null||!SpearMechanics.owned(p))return;
  if(CombatMath.strength(PlayerHooks.state(p).attackTicks,0,CombatHooks.speed(p))>=1){Packets.channel.sendToServer(new Packets.Jab());PlayerHooks.state(p).attackTicks=0;p.resetCooldown(); p.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);}
  ci.cancel();
 }
}
