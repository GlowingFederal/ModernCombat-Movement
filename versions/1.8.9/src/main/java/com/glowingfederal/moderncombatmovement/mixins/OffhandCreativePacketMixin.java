package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.offhand.Offhands;
@Mixin(value=NetHandlerPlayServer.class,remap=false)
public abstract class OffhandCreativePacketMixin {
 @Inject(method={"processCreativeInventoryAction","func_147344_a"},at=@At("HEAD"),cancellable=true)private void mcm$creative(C10PacketCreativeInventoryAction packet,CallbackInfo ci){
  NetHandlerPlayServer handler=(NetHandlerPlayServer)(Object)this;
  net.minecraft.network.PacketThreadUtil.checkThreadAndEnqueue(packet,handler,handler.playerEntity.getServerForPlayer());
  if(Offhands.creative(handler.playerEntity,packet.getSlotId(),packet.getStack()))ci.cancel();
 }
}
