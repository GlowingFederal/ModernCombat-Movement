package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.client.renderer.ItemRenderer.class,remap=false)
public abstract class SpearItemMotionMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"renderItemSide","func_187462_a"},at=@org.spongepowered.asm.mixin.injection.At("HEAD"))private void mcm$before(net.minecraft.entity.EntityLivingBase entity,net.minecraft.item.ItemStack stack,net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType type,boolean left,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){
  org.lwjgl.opengl.GL11.glPushMatrix();
  if(!(entity instanceof net.minecraft.entity.player.EntityPlayer)||com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)==null)return;
  if((type==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND||type==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND)){com.glowingfederal.moderncombatmovement.render.SpearItemMotion.third((net.minecraft.entity.player.EntityPlayer)entity,stack,left?-1:1,0);}
  
 }
 @org.spongepowered.asm.mixin.injection.Inject(method={"renderItemSide","func_187462_a"},at=@org.spongepowered.asm.mixin.injection.At("RETURN"))private void mcm$after(net.minecraft.entity.EntityLivingBase entity,net.minecraft.item.ItemStack stack,net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType type,boolean left,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){org.lwjgl.opengl.GL11.glPopMatrix();}
}
