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
  EntityPlayer p=Minecraft.getMinecraft().thePlayer;if(p==null)return;
  if(!SpearMechanics.owned(p)){net.minecraft.util.MovingObjectPosition hit=Minecraft.getMinecraft().objectMouseOver;if(com.glowingfederal.moderncombatmovement.config.Settings.combat(true)&&CombatHooks.profile(p)!=null&&(hit==null||hit.typeOfHit==net.minecraft.util.MovingObjectPosition.MovingObjectType.MISS)){Packets.channel.sendToServer(new Packets.Miss(p));PlayerHooks.state(p).attackTicks=0;}return;}
  if(CombatMath.strength(PlayerHooks.state(p).attackTicks,0,CombatHooks.speed(p))>=1){Packets.channel.sendToServer(new Packets.Jab());PlayerHooks.state(p).attackTicks=0; p.swingItem();}
  ci.cancel();
 }
}
