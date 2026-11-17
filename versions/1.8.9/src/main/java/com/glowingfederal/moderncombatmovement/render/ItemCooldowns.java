package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import com.glowingfederal.moderncombatmovement.compat.*;
import com.glowingfederal.moderncombatmovement.pose.*;
/** Inventory and hotbar share the native item overlay path and the actual gameplay timers. */
public final class ItemCooldowns {
 private ItemCooldowns(){}
 public static float fraction(ItemStack stack,float partial){
  Minecraft mc=Minecraft.getMinecraft();if(mc.thePlayer==null||stack==null)return 0;
  PlayerState state=PlayerHooks.state(mc.thePlayer);int ticks=0,total=1;
  if(Shields.known(stack)){ticks=state.shieldCooldown;total=100;}
  else if(WindCharges.known(stack)){ticks=state.windCooldown;total=10;}
  else if(stack.getItem()==net.minecraft.init.Items.ender_pearl){ticks=state.pearlCooldown;total=20;}
  return Math.max(0,Math.min(1,(ticks-partial)/total));
 }
 public static void draw(ItemStack stack,int x,int y){
  if(!com.glowingfederal.moderncombatmovement.config.ClientOptions.cooldownOverlays)return;
  float amount=fraction(stack,ClientProxy.framePartial);if(amount<=0)return;
  org.lwjgl.opengl.GL11.glPushAttrib(org.lwjgl.opengl.GL11.GL_ENABLE_BIT|org.lwjgl.opengl.GL11.GL_CURRENT_BIT|org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT|org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT);
  try{org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_LIGHTING);org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_DEPTH_TEST);net.minecraft.client.gui.Gui.drawRect(x,y+(int)Math.floor(16*(1-amount)),x+16,y+16,0x7FFFFFFF);}
  finally{org.lwjgl.opengl.GL11.glPopAttrib();}
 }
}
