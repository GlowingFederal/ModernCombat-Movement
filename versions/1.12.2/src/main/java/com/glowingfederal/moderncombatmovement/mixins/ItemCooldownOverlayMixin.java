package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.client.renderer.RenderItem.class,remap=false)
public abstract class ItemCooldownOverlayMixin {
 @org.spongepowered.asm.mixin.injection.Redirect(method={"renderItemOverlayIntoGUI","func_180453_a"},at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Lnet/minecraft/util/CooldownTracker;getCooldown(Lnet/minecraft/item/Item;F)F"),require=0)
 private float mcm$cooldown(net.minecraft.util.CooldownTracker tracker,net.minecraft.item.Item item,float partial){return com.glowingfederal.moderncombatmovement.config.ClientOptions.cooldownOverlays?tracker.getCooldown(item,partial):0;}
 @org.spongepowered.asm.mixin.injection.Redirect(method={"renderItemOverlayIntoGUI","func_180453_a"},at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Lnet/minecraft/util/CooldownTracker;func_185143_a(Lnet/minecraft/item/Item;F)F"),require=0)
 private float mcm$cooldownSrg(net.minecraft.util.CooldownTracker tracker,net.minecraft.item.Item item,float partial){return mcm$cooldown(tracker,item,partial);}
}
