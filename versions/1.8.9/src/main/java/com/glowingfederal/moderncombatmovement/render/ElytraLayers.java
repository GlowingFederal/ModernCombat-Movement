package com.glowingfederal.moderncombatmovement.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
public final class ElytraLayers implements LayerRenderer<AbstractClientPlayer> {
 private static boolean installed;
 public static void install(){if(installed||Minecraft.getMinecraft().getRenderManager()==null)return;for(RenderPlayer renderer:Minecraft.getMinecraft().getRenderManager().getSkinMap().values())renderer.addLayer(new ElytraLayers());installed=true;}
 public void doRenderLayer(AbstractClientPlayer p,float limb,float amount,float partial,float age,float yaw,float pitch,float scale){ElytraWings.render(p,partial,scale);}
 public boolean shouldCombineTextures(){return false;}
}
