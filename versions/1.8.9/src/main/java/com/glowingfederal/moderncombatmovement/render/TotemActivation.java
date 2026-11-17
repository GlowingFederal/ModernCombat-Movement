/* Native 1.12 EntityRenderer item activation and ParticleEmitter, adapted to legacy APIs. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

/** Uses the native particle manager and GUI pass; no second particle or item engine. */
public final class TotemActivation {
 static net.minecraft.client.renderer.texture.TextureAtlasSprite atlas;
 private static ItemStack activation;
 private static int ticks;
 private static float offX,offY;
 public static void init(){TotemActivation events=new TotemActivation();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(events);net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(events);}
 public static void activate(EntityLivingBase entity){
  Minecraft mc=Minecraft.getMinecraft();
  if(entity.worldObj!=mc.theWorld)return;
  entity.worldObj.playSound(entity.posX,entity.posY,entity.posZ,"moderncombatmovement:item.totem.use",1,1,false);
  mc.effectRenderer.addEffect(new TotemEmitter(entity));
  if(entity==mc.thePlayer){
   Item item=net.minecraftforge.fml.common.registry.GameRegistry.findItem(com.glowingfederal.moderncombatmovement.compat.Content.uab?"etfuturum":"moderncombatmovement","totem_of_undying");
   if(item!=null){activation=new ItemStack(item);ticks=40;offX=entity.getRNG().nextFloat()*2-1;offY=entity.getRNG().nextFloat()*2-1;}
  }
 }
 @SubscribeEvent public void stitch(TextureStitchEvent.Pre e){if(e.map==Minecraft.getMinecraft().getTextureMapBlocks()){atlas=e.map.registerSprite(new net.minecraft.util.ResourceLocation("moderncombatmovement:blocks/totem_particles"));TotemParticle.stitched();}}
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e){if(e.phase!=TickEvent.Phase.END)return;if(Minecraft.getMinecraft().theWorld==null){ticks=0;activation=null;}else if(ticks>0&&--ticks==0)activation=null;}
 @SubscribeEvent public void render(RenderGameOverlayEvent.Post e){
  if(e.type!=RenderGameOverlayEvent.ElementType.ALL||activation==null||ticks<=0)return;
  float f=(40-ticks+e.partialTicks)/40F,f1=f*f,f2=f*f1;
  float f3=10.25F*f2*f1-24.95F*f1*f1+25.5F*f2-13.8F*f1+4*f,angle=f3*(float)Math.PI;
  int width=e.resolution.getScaledWidth(),height=e.resolution.getScaledHeight();
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT|GL11.GL_TEXTURE_BIT);GL11.glPushMatrix();
  try{
   GL11.glEnable(GL11.GL_ALPHA_TEST);GL11.glEnable(GL11.GL_DEPTH_TEST);GL11.glDepthMask(true);GL11.glDisable(GL11.GL_CULL_FACE);GL11.glColor4f(1,1,1,1);RenderHelper.enableStandardItemLighting();
   GL11.glTranslatef(width/2+offX*(width/4)*Math.abs((float)Math.sin(angle*2)),height/2+offY*(height/4)*Math.abs((float)Math.sin(angle*2)),-50);
   float size=50+175*(float)Math.sin(angle);GL11.glScalef(size,-size,size);
   GL11.glRotatef(900*Math.abs((float)Math.sin(angle)),0,1,0);GL11.glRotatef(6*(float)Math.cos(f*8),1,0,0);GL11.glRotatef(6*(float)Math.cos(f*8),0,0,1);
   Minecraft.getMinecraft().getRenderItem().renderItem(activation,net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIXED);
  }finally{RenderHelper.disableStandardItemLighting();GL11.glPopMatrix();GL11.glPopAttrib();}
 }
}
