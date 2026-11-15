package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=GuiInventory.class,remap=false)
public abstract class OffhandSlotBackgroundMixin extends net.minecraft.client.gui.GuiScreen {
 @Inject(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At("RETURN"))private void mcm$slot(float partial,int mx,int my,CallbackInfo ci){int x=(width-176)/2+76,y=(height-166)/2+61;drawRect(x,y,x+18,y+18,0xff373737);drawRect(x+1,y+1,x+18,y+18,0xffffffff);drawRect(x+1,y+1,x+17,y+17,0xff8b8b8b);}
}
