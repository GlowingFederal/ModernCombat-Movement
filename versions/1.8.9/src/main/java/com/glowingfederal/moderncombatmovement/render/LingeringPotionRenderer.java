package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
public final class LingeringPotionRenderer extends RenderSnowball {
 public LingeringPotionRenderer(RenderManager manager){super(manager,com.glowingfederal.moderncombatmovement.compat.PotionDelivery.lingering,net.minecraft.client.Minecraft.getMinecraft().getRenderItem());}
 @Override public ItemStack func_177082_d(Entity entity){return ((com.glowingfederal.moderncombatmovement.compat.LingeringProjectile)entity).getStack();}
}
