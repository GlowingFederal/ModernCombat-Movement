package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
/** Native RenderPlayer held-item categories and render passes, mirrored at the actual offhand arm attachment. */
public final class LegacyHeldItem {
 private LegacyHeldItem(){}
 public static void draw(EntityPlayer player,ItemStack stack){
  int passes=stack.getItem().requiresMultipleRenderPasses()?stack.getItem().getRenderPasses(stack.getItemDamage()):1;
  for(int pass=0;pass<passes;pass++){int colour=stack.getItem().getColorFromItemStack(stack,pass);GL11.glColor4f((colour>>16&255)/255F,(colour>>8&255)/255F,(colour&255)/255F,1);Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem(player,stack,pass);}
 }
 public static void third(EntityPlayer player,ItemStack stack){
  GL11.glScalef(-1,1,1);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glTranslatef(-.0625F,.4375F,.0625F);
  net.minecraftforge.client.IItemRenderer renderer=net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(stack,net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
  boolean block3d=renderer!=null&&renderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED,stack,net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D);
  if(block3d||stack.getItem() instanceof ItemBlock&&net.minecraft.client.renderer.RenderBlocks.renderItemIn3d(net.minecraft.block.Block.getBlockFromItem(stack.getItem()).getRenderType())){GL11.glTranslatef(0,.1875F,-.3125F);GL11.glRotatef(20,1,0,0);GL11.glRotatef(45,0,1,0);GL11.glScalef(-.375F,-.375F,.375F);}
  else if(stack.getItem()==net.minecraft.init.Items.bow){GL11.glTranslatef(0,.125F,.3125F);GL11.glRotatef(-20,0,1,0);GL11.glScalef(.625F,-.625F,.625F);GL11.glRotatef(-100,1,0,0);GL11.glRotatef(45,0,1,0);}
  else if(stack.getItem().isFull3D()){if(stack.getItem().shouldRotateAroundWhenRendering()){GL11.glRotatef(180,0,0,1);GL11.glTranslatef(0,-.125F,0);}GL11.glTranslatef(0,.1875F,0);GL11.glScalef(.625F,-.625F,.625F);GL11.glRotatef(-100,1,0,0);GL11.glRotatef(45,0,1,0);}
  else{GL11.glTranslatef(.25F,.1875F,-.1875F);GL11.glScalef(.375F,.375F,.375F);GL11.glRotatef(60,0,0,1);GL11.glRotatef(-90,1,0,0);GL11.glRotatef(20,0,0,1);}
  draw(player,stack);
 }
}
