package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=LayerHeldItem.class,remap=false)
public abstract class OffhandRenderMixin {
 @Shadow(aliases="field_177206_a") @Final private RendererLivingEntity<?> livingEntityRenderer;
 @Inject(method={"doRenderLayer","func_177141_a"},at=@At("RETURN"))private void mcm$held(EntityLivingBase entity,float limb,float amount,float partial,float age,float yaw,float pitch,float scale,CallbackInfo ci){if(entity instanceof EntityPlayer&&livingEntityRenderer.getMainModel() instanceof net.minecraft.client.model.ModelBiped)com.glowingfederal.moderncombatmovement.render.OffhandClient.held((net.minecraft.client.model.ModelBiped)livingEntityRenderer.getMainModel(),(EntityPlayer)entity);}
}
