package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.client.renderer.entity.RenderItem.class,remap=false)
public abstract class ItemCooldownOverlayMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V","func_94148_a(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"},at=@org.spongepowered.asm.mixin.injection.At("RETURN"))
 private void mcm$cooldown(net.minecraft.client.gui.FontRenderer font,net.minecraft.client.renderer.texture.TextureManager textures,net.minecraft.item.ItemStack stack,int x,int y,String text,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){com.glowingfederal.moderncombatmovement.render.ItemCooldowns.draw(stack,x,y);}
}
