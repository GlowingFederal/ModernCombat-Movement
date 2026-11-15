package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.render.OffhandClient;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mixin(value=GuiContainer.class,remap=false)
public abstract class OffhandGuiMixin {
 @Shadow(aliases="field_147006_u")private Slot theSlot;
 @Inject(method={"keyTyped","func_73869_a"},at=@At("HEAD"),cancellable=true)private void mcm$swap(char typed,int key,CallbackInfo ci){
  net.minecraft.client.Minecraft mc=net.minecraft.client.Minecraft.getMinecraft();
  if(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainerCreative&&theSlot!=null&&theSlot.inventory!=mc.thePlayer.inventory&&!(theSlot.inventory instanceof com.glowingfederal.moderncombatmovement.offhand.OffhandInventory)){if(key==OffhandClient.SWAP.getKeyCode())ci.cancel();return;}
  if(key==OffhandClient.SWAP.getKeyCode()&&theSlot!=null&&mc.thePlayer.inventory.getItemStack()==null){Packets.channel.sendToServer(new Packets.OffhandAction(mc.thePlayer,theSlot.slotNumber));ci.cancel();}
 }
}
