/* Adapted from UAB LingeringPotionRenderer; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;


import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.compat.LingeringProjectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class LingeringPotionRenderer extends RenderSnowball {

	public LingeringPotionRenderer() {
		super(com.glowingfederal.moderncombatmovement.compat.PotionDelivery.lingering);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		if (!(entity instanceof LingeringProjectile))
			return;

		ItemStack stack = ((LingeringProjectile) entity).getStack();
		if (stack == null || stack.getItem() == null)
			return;

		int passes;
		if (stack.getItem().requiresMultipleRenderPasses())
			passes = stack.getItem().getRenderPasses(0);
		else
			passes = 1;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glEnable(32826);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		bindEntityTexture(entity);

		for (int pass = 0; pass < passes; pass++) {
			IIcon icon = stack.getItem().getIcon(stack, pass);

			if (icon != null) {
				GL11.glPushMatrix();
				colour(stack.getItem().getColorFromItemStack(stack, pass));
				renderIcon(icon);
				GL11.glPopMatrix();
			}
		}

		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

    private static void colour(int c){GL11.glColor3f((c>>16&255)/255F,(c>>8&255)/255F,(c&255)/255F);}
	private void renderIcon(IIcon icon) {
		Tessellator tessellator = Tessellator.instance;

		float minU = icon.getMinU();
		float maxU = icon.getMaxU();
		float minV = icon.getMinV();
		float maxV = icon.getMaxV();

		GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-0.5F, -0.25F, 0.0D, minU, maxV);
		tessellator.addVertexWithUV(0.5F, -0.25F, 0.0D, maxU, maxV);
		tessellator.addVertexWithUV(0.5F, 0.75F, 0.0D, maxU, minV);
		tessellator.addVertexWithUV(-0.5F, 0.75F, 0.0D, minU, minV);
		tessellator.draw();
	}
}