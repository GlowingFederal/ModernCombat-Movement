package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.offhand.OffhandInventory;
/** Native creative remapping otherwise places every appended slot on top of the first hotbar slot. */
@Mixin(value=GuiContainerCreative.class,remap=false)
public abstract class OffhandCreativeGuiMixin extends net.minecraft.client.gui.GuiScreen {
 @Inject(method={"setCurrentCreativeTab","func_147050_b"},at=@At("RETURN"))private void mcm$tab(CreativeTabs tab,CallbackInfo ci){for(Object o:((GuiContainer)(Object)this).inventorySlots.inventorySlots){Slot slot=(Slot)o;if(slot.inventory instanceof OffhandInventory){slot.xDisplayPosition=95;slot.yDisplayPosition=20;}}}
 @Inject(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At("RETURN"))private void mcm$background(float partial,int mx,int my,CallbackInfo ci){for(Object o:((GuiContainer)(Object)this).inventorySlots.inventorySlots){Slot slot=(Slot)o;if(slot.inventory instanceof OffhandInventory){int x=(width-195)/2+slot.xDisplayPosition-1,y=(height-136)/2+slot.yDisplayPosition-1;drawRect(x,y,x+18,y+18,0xff373737);drawRect(x+1,y+1,x+18,y+18,0xffffffff);drawRect(x+1,y+1,x+17,y+17,0xff8b8b8b);}}}
}
