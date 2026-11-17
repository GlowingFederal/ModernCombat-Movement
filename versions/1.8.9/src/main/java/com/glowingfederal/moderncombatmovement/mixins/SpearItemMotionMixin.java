package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.client.renderer.ItemRenderer.class,remap=false)
public abstract class SpearItemMotionMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"renderItem","func_178099_a"},at=@org.spongepowered.asm.mixin.injection.At("HEAD"))private void mcm$before(net.minecraft.entity.EntityLivingBase entity,net.minecraft.item.ItemStack stack,net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType type,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){
  org.lwjgl.opengl.GL11.glPushMatrix();
  if(!(entity instanceof net.minecraft.entity.player.EntityPlayer)||com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)==null)return;
  if(type==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON){org.lwjgl.opengl.GL11.glTranslatef(.0625F,-.4375F,-.0625F);org.lwjgl.opengl.GL11.glRotatef(-90,1,0,0);org.lwjgl.opengl.GL11.glRotatef(180,0,1,0);org.lwjgl.opengl.GL11.glTranslatef(1/16F,.125F,-.625F);com.glowingfederal.moderncombatmovement.render.SpearItemMotion.third((net.minecraft.entity.player.EntityPlayer)entity,stack,1,0);}
  if(type==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON||type==net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.THIRD_PERSON)org.lwjgl.opengl.GL11.glScalef(.5F,.5F,.5F);
 }
 @org.spongepowered.asm.mixin.injection.Inject(method={"renderItem","func_178099_a"},at=@org.spongepowered.asm.mixin.injection.At("RETURN"))private void mcm$after(net.minecraft.entity.EntityLivingBase entity,net.minecraft.item.ItemStack stack,net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType type,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){org.lwjgl.opengl.GL11.glPopMatrix();}
}
