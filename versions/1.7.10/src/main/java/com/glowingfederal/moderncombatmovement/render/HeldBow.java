package com.glowingfederal.moderncombatmovement.render;
import org.lwjgl.opengl.GL11;
/** Native generated bow plane and first-person display, using the native bow's current pull icon. */
public final class HeldBow {
 private HeldBow(){}
 public static void render(net.minecraft.item.ItemStack stack,net.minecraft.entity.player.EntityPlayer player,int sign){
  net.minecraft.util.IIcon icon=stack.getItem().getIcon(stack,0,player,player.getItemInUse(),player.getItemInUseCount());if(icon==null)return;
  GL11.glTranslatef(sign*1.13F/16,3.2F/16,1.13F/16);GL11.glRotatef(-sign*90,0,1,0);GL11.glRotatef(sign*25,0,0,1);GL11.glScalef(.68F,.68F,.68F);GL11.glTranslatef(-.5F,-.5F,.03125F);
  net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationItemsTexture);
  net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(net.minecraft.client.renderer.Tessellator.instance,icon.getMinU(),icon.getMinV(),icon.getMaxU(),icon.getMaxV(),icon.getIconWidth(),icon.getIconHeight(),.0625F);
  ModelGlint.render(stack.isItemEnchanted(),player.ticksExisted,.125F,()->net.minecraft.client.renderer.ItemRenderer.renderItemIn2D(net.minecraft.client.renderer.Tessellator.instance,0,0,1,1,256,256,.0625F),net.minecraft.client.renderer.texture.TextureMap.locationItemsTexture);
 }
}
