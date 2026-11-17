package com.glowingfederal.moderncombatmovement.render;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.combat.SpearSpec;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
/** Native extruded held spear sprite; the GUI retains the ordinary item icon. */
public final class SpearItemRenderer implements IItemRenderer {
 private static final Map<SpearSpec,IIcon> ICONS=new EnumMap<SpearSpec,IIcon>(SpearSpec.class);
 @SubscribeEvent public void stitch(TextureStitchEvent.Pre event){if(event.map.getTextureType()==1)for(SpearSpec spec:SpearSpec.values())ICONS.put(spec,event.map.registerIcon("moderncombatmovement:"+spec.material+"_spear_in_hand"));}
 public boolean handleRenderType(ItemStack stack,ItemRenderType type){return type==ItemRenderType.EQUIPPED||type==ItemRenderType.EQUIPPED_FIRST_PERSON;}
 public boolean shouldUseRenderHelper(ItemRenderType type,ItemStack stack,ItemRendererHelper helper){return helper==ItemRendererHelper.EQUIPPED_BLOCK||type==ItemRenderType.EQUIPPED&&helper==ItemRendererHelper.BLOCK_3D;}
 public void renderItem(ItemRenderType type,ItemStack stack,Object... data){
  GL11.glPushMatrix();try{GL11.glTranslatef(.5F,.5F,.5F);if(type==ItemRenderType.EQUIPPED){GL11.glScalef(-1/.375F,-1/.375F,1/.375F);GL11.glRotatef(-45,0,1,0);GL11.glRotatef(-20,1,0,0);GL11.glTranslatef(.0625F,-.625F,.25F);GL11.glRotatef(-90,1,0,0);GL11.glRotatef(180,0,1,0);GL11.glTranslatef(1/16F,.125F,-.625F);if(data.length>1&&data[1] instanceof net.minecraft.entity.player.EntityPlayer)SpearItemMotion.third((net.minecraft.entity.player.EntityPlayer)data[1],stack,1,0);renderModern(stack,false);}else{GL11.glScalef(2.5F,2.5F,2.5F);GL11.glRotatef(-45,0,1,0);renderModern(stack,true);}}finally{GL11.glPopMatrix();}
 }
 public void renderModern(ItemStack stack,boolean first){
  renderModern(stack,first,1);
 }
 public void renderModern(ItemStack stack,boolean first,int sign){
  IIcon icon=ICONS.get(ModernEquipment.spear(stack));if(icon==null)return;
  GL11.glPushMatrix();try{if(first){GL11.glTranslatef(sign*3.13F/16,2F/16,.13F/16);GL11.glRotatef(-20,1,0,0);GL11.glRotatef(sign*90,0,1,0);GL11.glRotatef(sign*-35,0,0,1);GL11.glScalef(1.36F,1.36F,.68F);}else{GL11.glTranslatef(0,2F/16,2F/16);GL11.glRotatef(5,1,0,0);GL11.glRotatef(sign*270,0,1,0);GL11.glRotatef(sign*-40,0,0,1);GL11.glScalef(1.7F,1.7F,.85F);}
   GL11.glTranslatef(-.5F,-.5F,.03125F);GL11.glColor4f(1,1,1,1);Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationItemsTexture);
   ItemRenderer.renderItemIn2D(Tessellator.instance,icon.getMinU(),icon.getMinV(),icon.getMaxU(),icon.getMaxV(),icon.getIconWidth(),icon.getIconHeight(),.0625F);
   ModelGlint.render(stack.isItemEnchanted(),Minecraft.getSystemTime()/50F,.125F,()->ItemRenderer.renderItemIn2D(Tessellator.instance,1,0,0,1,256,256,.0625F),TextureMap.locationItemsTexture);
  }finally{GL11.glPopMatrix();}
 }
}
