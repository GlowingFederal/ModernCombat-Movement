/* Adapted from UAB; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class LingeringCloudRenderer extends Render {

	public LingeringCloudRenderer(net.minecraft.client.renderer.entity.RenderManager manager) {
        super(manager);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}