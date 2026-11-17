package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=GuiInventory.class,remap=false)
public abstract class OffhandSlotBackgroundMixin extends net.minecraft.client.gui.inventory.GuiContainer {
 protected OffhandSlotBackgroundMixin(net.minecraft.inventory.Container container){super(container);}
 @Inject(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At("RETURN"))private void mcm$slot(float partial,int mx,int my,CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.OffhandClient.slotBackground(guiLeft+76,guiTop+61);}
}
