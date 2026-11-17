package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.combat.*;
/** Native 1.12 attack indicator layout, driven by the legacy attack pipeline's attribute/tick state. */
public final class CombatHud extends Gui {
 private static final net.minecraft.util.ResourceLocation ICONS=new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/gui/attack_icons.png");
 @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public void draw(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer p=mc.thePlayer;
  String mode=com.glowingfederal.moderncombatmovement.config.ClientOptions.attackIndicator;
  if(p==null||mc.gameSettings.hideGUI||!com.glowingfederal.moderncombatmovement.config.Settings.combat(true)||CombatHooks.profile(p)==null||mode.equals("OFF"))return;
  boolean hotbar=mode.equals("HOTBAR");
  if(event.type!=(hotbar?net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR:net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.CROSSHAIRS))return;
  if(!hotbar&&mc.gameSettings.thirdPersonView!=0)return;
  float strength=CombatMath.strength(com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).attackTicks,0,CombatHooks.speed(p));
  int width=event.resolution.getScaledWidth(),height=event.resolution.getScaledHeight();
  org.lwjgl.opengl.GL11.glPushAttrib(org.lwjgl.opengl.GL11.GL_ENABLE_BIT|org.lwjgl.opengl.GL11.GL_CURRENT_BIT|org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT);
  try{org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_LIGHTING);org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);org.lwjgl.opengl.GL11.glBlendFunc(770,771);org.lwjgl.opengl.GL11.glColor4f(1,1,1,1);mc.getTextureManager().bindTexture(ICONS);
   if(hotbar){if(strength<1){int x=width/2+97,y=height-20,fill=(int)(strength*19);drawTexturedModalRect(x,y,0,94,18,18);drawTexturedModalRect(x,y+18-fill,18,112-fill,18,fill);}}
   else{int x=width/2-8,y=height/2+9;
    if(strength>=1&&20/CombatHooks.speed(p)>5&&mc.pointedEntity instanceof net.minecraft.entity.EntityLivingBase&&((net.minecraft.entity.EntityLivingBase)mc.pointedEntity).isEntityAlive())drawTexturedModalRect(x,y,68,94,16,16);
    else if(strength<1){drawTexturedModalRect(x,y,36,94,16,4);drawTexturedModalRect(x,y,52,94,(int)(strength*17),4);}
   }
  }finally{org.lwjgl.opengl.GL11.glPopAttrib();}
 }
}
