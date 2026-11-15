package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import com.glowingfederal.moderncombatmovement.offhand.*;
import com.glowingfederal.moderncombatmovement.network.Packets;
public final class OffhandClient extends net.minecraft.client.gui.Gui {
 public static final KeyBinding SWAP=new KeyBinding("key.moderncombatmovement.swapHands",Keyboard.KEY_F,"key.categories.inventory");
 public static boolean handled;
 private static EntityPlayer renderOwner;
 private static ItemStack rendered;
 private static float equip=1,previousEquip=1;
 private static void equipment(EntityPlayer p){
  if(p!=renderOwner){renderOwner=p;rendered=p==null?null:Offhands.get(p);equip=previousEquip=1;return;}
  previousEquip=equip;ItemStack next=p==null?null:Offhands.get(p);
  boolean same=rendered==next||rendered!=null&&next!=null&&rendered.getItem()==next.getItem()&&(!rendered.getHasSubtypes()||rendered.getItemDamage()==next.getItemDamage());
  equip+=Math.max(-.4F,Math.min(.4F,(same?1:0)-equip));
  if(equip<.1F||same)rendered=next;
 }

 public static void tryUse(){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer p=mc.thePlayer;if(!com.glowingfederal.moderncombatmovement.config.Settings.offhand(true)||!Offhands.allowed(p)||!OffhandUses.supported(Offhands.get(p)))return;
  Packets.OffhandUse request=new Packets.OffhandUse(p,false);net.minecraft.util.MovingObjectPosition hit=mc.objectMouseOver;
  if(hit!=null&&hit.typeOfHit==net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK){request.x=hit.blockX;request.y=hit.blockY;request.z=hit.blockZ;request.side=hit.sideHit;request.hx=(float)Math.max(0,Math.min(1,hit.hitVec.xCoord-request.x));request.hy=(float)Math.max(0,Math.min(1,hit.hitVec.yCoord-request.y));request.hz=(float)Math.max(0,Math.min(1,hit.hitVec.zCoord-request.z));}
  if(hit!=null&&hit.typeOfHit==net.minecraft.util.MovingObjectPosition.MovingObjectType.ENTITY&&hit.entityHit!=null)request.entity=hit.entityHit.getEntityId();
  Packets.channel.sendToServer(request);
  if(request.side<0&&request.entity<0)OffhandUses.useAir(p);
 }
 private OffhandClient(){}
 public static void init(){cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(SWAP);net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new OffhandClient());}
 @cpw.mods.fml.common.eventhandler.SubscribeEvent public void hud(net.minecraftforge.client.event.RenderGameOverlayEvent.Post e){
  if(e.type!=net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR)return;Minecraft mc=Minecraft.getMinecraft();ItemStack s=mc.thePlayer==null?null:Offhands.get(mc.thePlayer);if(s==null)return;
  int x=e.resolution.getScaledWidth()/2-118,y=e.resolution.getScaledHeight()-22;drawRect(x-1,y-1,x+19,y+19,0x99000000);drawRect(x,y,x+18,y+18,0xff8b8b8b);
  net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();new net.minecraft.client.renderer.entity.RenderItem().renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),s,x+1,y+1);new net.minecraft.client.renderer.entity.RenderItem().renderItemOverlayIntoGUI(mc.fontRenderer,mc.getTextureManager(),s,x+1,y+1);;net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
 }
 public static void tick(){Minecraft mc=Minecraft.getMinecraft();equipment(mc.thePlayer);while(SWAP.isPressed())if(mc.thePlayer!=null&&mc.currentScreen==null)Packets.channel.sendToServer(new Packets.OffhandAction(mc.thePlayer,-1));}
 public static void apply(EntityPlayer p,Packets.Snapshot packet){
  OffhandInventory hand=Offhands.inventory(p);if(hand.swingSerial!=packet.offhandSwingSerial){hand.swingSerial=packet.offhandSwingSerial;hand.swingTicks=packet.offhandSwing;}
  com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).shieldCooldown=packet.shieldCooldown;com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).windCooldown=packet.windCooldown;
  if(!ItemStack.areItemStacksEqual(Offhands.get(p),packet.offhand))Offhands.set(p,packet.offhand);
  if(packet.offhandUse>0&&Offhands.get(p)!=null){p.setItemInUse(Offhands.get(p),packet.offhandUse);((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useRemaining(packet.offhandUse);}
  else if(Offhands.using(p))p.clearItemInUse();
 }
 public static void pose(net.minecraft.client.model.ModelBiped model,EntityPlayer p){
  float swing=swing(p,0);if(swing>0)model.bipedLeftArm.rotateAngleX-=(float)Math.sin(Math.sqrt(swing)*Math.PI)*1.2F;
  ItemStack stack=Offhands.get(p);if(stack!=null&&Offhands.using(p)){
   model.bipedLeftArm.rotateAngleX=(com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)?-(float)Math.PI:-1.3F)+model.bipedHead.rotateAngleX;
   model.bipedLeftArm.rotateAngleY=.3F+model.bipedHead.rotateAngleY;
  }
 }
 public static void held(net.minecraft.client.model.ModelBiped model,EntityPlayer p){
  ItemStack stack=Offhands.get(p);if(stack==null||p.isInvisible())return;
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();
  try{model.bipedLeftArm.postRender(.0625F);GL11.glTranslatef(.0625F,.4375F,.0625F);GL11.glRotatef(-90,1,0,0);GL11.glRotatef(180,0,1,0);GL11.glScalef(.625F,.625F,.625F);Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem(p,stack,0);}
  finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }
 private static float swing(EntityPlayer p,float partial){int ticks=Offhands.inventory(p).swingTicks;return ticks<=0?0:Math.min(1,(6-ticks+partial)/6F);}
 public static void firstPerson(float partial){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer p=mc.thePlayer;ItemStack stack=p==renderOwner?rendered:p==null?null:Offhands.get(p);if(stack==null||p.isInvisible())return;
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();
  try{net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();GL11.glTranslatef(-.56F,-.52F-(1-(previousEquip+(equip-previousEquip)*partial))*.6F,-.72F);if(Offhands.using(p)){GL11.glTranslatef(.14F,.12F,-.10F);GL11.glRotatef(-25,0,1,0);}float swing=swing(p,partial);if(swing>0){float root=(float)Math.sqrt(swing);GL11.glTranslatef((float)Math.sin(root*Math.PI)*.4F,(float)Math.sin(root*Math.PI*2)*.2F,-(float)Math.sin(swing*Math.PI)*.2F);GL11.glRotatef((float)Math.sin(root*Math.PI)*20,0,0,1);}
   GL11.glRotatef(45,0,1,0);GL11.glScalef(.5F,.5F,.5F);mc.entityRenderer.itemRenderer.renderItem(p,stack,0);}
  finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }
}
