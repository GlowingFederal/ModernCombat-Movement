/* Adapted from UAB TridentRenderer; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;

import com.glowingfederal.moderncombatmovement.compat.TridentProjectile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.ResourceLocation;

public final class TridentRenderer extends Render<TridentProjectile> {
    private final TridentModel model = new TridentModel();
    public TridentRenderer(RenderManager manager) {
        super(manager);
    }
    @Override protected ResourceLocation getEntityTexture(TridentProjectile entity) { return TridentModel.TEXTURE; }
    @Override public void doRender(TridentProjectile entity, double x, double y, double z, float yaw, float partial) {
        bindEntityTexture(entity); GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partial - 90, 0, 1, 0);
            GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partial + 90, 0, 0, 1);
            model.render();
            ModelGlint.render(entity.enchanted(),entity.ticksExisted+partial,.33333334F,()->model.render(),TridentModel.TEXTURE);
        } finally { GlStateManager.popMatrix(); }
        super.doRender(entity, x, y, z, yaw, partial);
    }
}
