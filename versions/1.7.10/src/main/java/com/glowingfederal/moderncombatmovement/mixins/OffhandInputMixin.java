package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.render.OffhandClient;
/** Keep native main-hand/block/entity arbitration. Only its unconsumed fallback reaches the offhand. */
@Mixin(value=Minecraft.class,remap=false)
public abstract class OffhandInputMixin {
 @Unique private int mcm$mainCount;
 @Unique private ItemStack mcm$main;
 @Inject(method={"rightClickMouse","func_147121_ag"},at=@At("HEAD"))private void mcm$before(CallbackInfo ci){OffhandClient.handled=false;EntityPlayer p=Minecraft.getMinecraft().thePlayer;mcm$main=p.inventory.getCurrentItem();mcm$mainCount=mcm$main==null?0:mcm$main.stackSize;}
 @Inject(method={"rightClickMouse","func_147121_ag"},at=@At("RETURN"))private void mcm$fallback(CallbackInfo ci){EntityPlayer p=Minecraft.getMinecraft().thePlayer;if(!OffhandClient.handled&&!p.isUsingItem()&&p.inventory.getCurrentItem()==mcm$main&&(mcm$main==null||mcm$main.stackSize==mcm$mainCount))OffhandClient.tryUse();}
 @Inject(method={"clickMouse","func_147116_af"},at=@At("HEAD"),cancellable=true)private void mcm$using(CallbackInfo ci){EntityPlayer p=Minecraft.getMinecraft().thePlayer;if(p!=null&&p.isUsingItem())ci.cancel();}
}
