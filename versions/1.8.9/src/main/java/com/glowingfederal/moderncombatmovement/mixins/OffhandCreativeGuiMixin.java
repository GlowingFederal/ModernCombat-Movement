package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.offhand.OffhandInventory;
/** Vanilla offhand layout also moves the old creative armor slots and player preview. */
@Mixin(value=GuiContainerCreative.class,remap=false)
public abstract class OffhandCreativeGuiMixin extends net.minecraft.client.gui.GuiScreen {
 @Inject(method={"setCurrentCreativeTab","func_147050_b"},at=@At("RETURN"))private void mcm$tab(CreativeTabs tab,CallbackInfo ci){
  if(tab!=CreativeTabs.tabInventory)return;
  java.util.List slots=((GuiContainer)(Object)this).inventorySlots.inventorySlots;
  for(int i=0;i<slots.size();i++){
   Slot slot=(Slot)slots.get(i);
   if(slot.inventory instanceof OffhandInventory){slot.xDisplayPosition=35;slot.yDisplayPosition=20;}
   else if(i>=5&&i<9&&slot.inventory==Minecraft.getMinecraft().thePlayer.inventory){int armor=i-5;slot.xDisplayPosition=54+armor/2*54;slot.yDisplayPosition=6+armor%2*27;}
  }
 }
 @Group(name="mcmCreativeTexture",min=1) @Redirect(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"),require=0)
 private void mcm$textureMcp(TextureManager manager,ResourceLocation texture){mcm$texture(manager,texture);}
 @Group(name="mcmCreativeTexture",min=1) @Redirect(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/texture/TextureManager;func_110577_a(Lnet/minecraft/util/ResourceLocation;)V"),require=0)
 private void mcm$textureSrg(TextureManager manager,ResourceLocation texture){mcm$texture(manager,texture);}
 private void mcm$texture(TextureManager manager,ResourceLocation texture){manager.bindTexture("textures/gui/container/creative_inventory/tab_inventory.png".equals(texture.getResourcePath())?new ResourceLocation("moderncombatmovement:textures/gui/offhand_creative_inventory.png"):texture);}
 @Group(name="mcmCreativePlayer",min=1) @Redirect(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/inventory/GuiInventory;drawEntityOnScreen(IIIFFLnet/minecraft/entity/EntityLivingBase;)V"),require=0)
 private void mcm$playerMcp(int x,int y,int scale,float lookX,float lookY,EntityLivingBase entity){mcm$player(x,y,scale,lookX,lookY,entity);}
 @Group(name="mcmCreativePlayer",min=1) @Redirect(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/inventory/GuiInventory;func_147046_a(IIIFFLnet/minecraft/entity/EntityLivingBase;)V"),require=0)
 private void mcm$playerSrg(int x,int y,int scale,float lookX,float lookY,EntityLivingBase entity){mcm$player(x,y,scale,lookX,lookY,entity);}
 private void mcm$player(int x,int y,int scale,float lookX,float lookY,EntityLivingBase entity){GuiInventory.drawEntityOnScreen(x+45,y,scale,lookX+45,lookY,entity);}
 @Inject(method={"drawGuiContainerBackgroundLayer","func_146976_a"},at=@At("RETURN"))private void mcm$background(float partial,int mx,int my,CallbackInfo ci){for(Object o:((GuiContainer)(Object)this).inventorySlots.inventorySlots){Slot slot=(Slot)o;if(slot.inventory instanceof OffhandInventory){int x=(width-195)/2+slot.xDisplayPosition-1,y=(height-136)/2+slot.yDisplayPosition-1;com.glowingfederal.moderncombatmovement.render.OffhandClient.slotBackground(x,y);}}}
}
