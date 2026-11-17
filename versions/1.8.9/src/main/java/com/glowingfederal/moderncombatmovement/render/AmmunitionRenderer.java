/* UAB spectral-arrow texture selection on the native arrow renderer; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import com.glowingfederal.moderncombatmovement.compat.CrossbowBolt;
public final class AmmunitionRenderer extends RenderArrow {
 private static final ResourceLocation SPECTRAL=new ResourceLocation("moderncombatmovement:textures/entity/projectiles/spectral_arrow.png");
 public AmmunitionRenderer(net.minecraft.client.renderer.entity.RenderManager manager){super(manager);}
 @Override protected ResourceLocation getEntityTexture(EntityArrow entity){return entity instanceof CrossbowBolt&&((CrossbowBolt)entity).spectral()?SPECTRAL:super.getEntityTexture(entity);}
}
