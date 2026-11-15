package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.offhand.Offhands;
import com.glowingfederal.moderncombatmovement.render.OffhandClient;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mixin(value=PlayerControllerMP.class,remap=false)
public abstract class OffhandControllerMixin {
 @Inject(method={"onPlayerRightClick","func_178890_a","func_78760_a","interactWithEntitySendPacket","func_78768_b","isPlayerRightClickingOnEntity","func_178894_a"},at=@At("RETURN"))private void mcm$accepted(CallbackInfoReturnable<Boolean> ci){if(ci.getReturnValue())OffhandClient.handled=true;}
 @Inject(method={"onStoppedUsingItem","func_78766_c"},at=@At("HEAD"),cancellable=true)private void mcm$stop(EntityPlayer p,CallbackInfo ci){if(Offhands.using(p)){Packets.channel.sendToServer(new Packets.OffhandUse(p,true));p.stopUsingItem();ci.cancel();}}
}
