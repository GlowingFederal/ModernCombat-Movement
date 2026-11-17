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
 public static boolean useHeld(){Minecraft mc=Minecraft.getMinecraft();return mc.thePlayer!=null&&mc.currentScreen==null&&mc.inGameHasFocus&&org.lwjgl.opengl.Display.isActive()&&mc.gameSettings.keyBindUseItem.getIsKeyPressed();}
 private OffhandClient(){}
 public static void init(){cpw.mods.fml.client.registry.ClientRegistry.registerKeyBinding(SWAP);net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new OffhandClient());}
 @cpw.mods.fml.common.eventhandler.SubscribeEvent public void hud(net.minecraftforge.client.event.RenderGameOverlayEvent.Post e){
  if(e.type!=net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HOTBAR)return;Minecraft mc=Minecraft.getMinecraft();ItemStack s=mc.thePlayer==null?null:Offhands.get(mc.thePlayer);if(s==null)return;
  int x=e.resolution.getScaledWidth()/2-118,y=e.resolution.getScaledHeight()-20;mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/gui/offhand_widgets.png"));GL11.glColor4f(1,1,1,1);drawTexturedModalRect(x-2,y-3,24,22,29,24);
  net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();new net.minecraft.client.renderer.entity.RenderItem().renderItemAndEffectIntoGUI(mc.fontRenderer,mc.getTextureManager(),s,x+1,y+1);new net.minecraft.client.renderer.entity.RenderItem().renderItemOverlayIntoGUI(mc.fontRenderer,mc.getTextureManager(),s,x+1,y+1);;net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
 }
 public static void reset(){equipment(null);handled=false;}
 public static void tick(){Minecraft mc=Minecraft.getMinecraft();equipment(mc.thePlayer);
  if(mc.thePlayer!=null&&Offhands.using(mc.thePlayer)&&!useHeld()){Packets.channel.sendToServer(new Packets.OffhandUse(mc.thePlayer,true));mc.thePlayer.stopUsingItem();}
while(SWAP.isPressed())if(mc.thePlayer!=null&&mc.currentScreen==null)Packets.channel.sendToServer(new Packets.OffhandAction(mc.thePlayer,-1));}
 public static void apply(EntityPlayer p,Packets.Snapshot packet){
  OffhandInventory hand=Offhands.inventory(p);if(hand.swingSerial!=packet.offhandSwingSerial){hand.swingSerial=packet.offhandSwingSerial;hand.swingTicks=packet.offhandSwing;}
  com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).shieldCooldown=packet.shieldCooldown;com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).windCooldown=packet.windCooldown;com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).pearlCooldown=packet.pearlCooldown;
  if(!ItemStack.areItemStacksEqual(Offhands.get(p),packet.offhand))Offhands.set(p,packet.offhand);
  boolean local=p==Minecraft.getMinecraft().thePlayer;
  if(local&&!com.glowingfederal.moderncombatmovement.pose.UseSequence.acknowledges(packet.offhandAckSerial,com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).offhandIntentSerial))return;
  if(local&&!useHeld()){if(Offhands.using(p))p.clearItemInUse();return;}
  if(packet.offhandUse>0&&Offhands.get(p)!=null){p.setItemInUse(Offhands.get(p),packet.offhandUse);((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useRemaining(packet.offhandUse);}
  else if(Offhands.using(p))p.clearItemInUse();
 }
 public static void pose(net.minecraft.client.model.ModelBiped model,EntityPlayer p){
  ItemStack use=((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack();
  if(Offhands.using(p)&&use!=null&&use.getItem() instanceof net.minecraft.item.ItemBow){
   model.bipedLeftArm.rotateAngleX=model.bipedRightArm.rotateAngleX=-(float)Math.PI/2+model.bipedHead.rotateAngleX;model.bipedLeftArm.rotateAngleY=.1F+model.bipedHead.rotateAngleY;model.bipedRightArm.rotateAngleY=-.5F+model.bipedHead.rotateAngleY;model.bipedLeftArm.rotateAngleZ=model.bipedRightArm.rotateAngleZ=0;return;
  }

  float swing=swing(p,0);if(swing>0)model.bipedLeftArm.rotateAngleX-=(float)Math.sin(Math.sqrt(swing)*Math.PI)*1.2F;
  ItemStack stack=Offhands.get(p);if(stack!=null&&Offhands.using(p)&&!com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)&&com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)==null&&!com.glowingfederal.moderncombatmovement.compat.Crossbows.known(stack)){
   model.bipedLeftArm.rotateAngleX=(com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)?-(float)Math.PI:-1.3F)+model.bipedHead.rotateAngleX;
   model.bipedLeftArm.rotateAngleY=.3F+model.bipedHead.rotateAngleY;
  }
 }
 public static void held(net.minecraft.client.model.ModelBiped model,EntityPlayer p){
  ItemStack stack=Offhands.get(p);if(stack==null||p.isInvisible())return;
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();
  try{boolean owned=com.glowingfederal.moderncombatmovement.compat.Shields.known(stack)||com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack);if(owned&&p.isSneaking())GL11.glTranslatef(0,.2F,0);model.bipedLeftArm.postRender(.0625F);if(com.glowingfederal.moderncombatmovement.compat.Crossbows.known(stack)){GL11.glRotatef(-90,1,0,0);GL11.glRotatef(180,0,1,0);GL11.glTranslatef(-1/16F,.125F,-.625F);GL11.glScalef(-1,1,1);GL11.glDisable(GL11.GL_CULL_FACE);new CrossbowItemRenderer().renderModernHand(stack,p);return;}if(com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)!=null){GL11.glRotatef(-90,1,0,0);GL11.glRotatef(180,0,1,0);GL11.glTranslatef(-1/16F,.125F,-.625F);SpearItemMotion.third(p,stack,-1,0);new SpearItemRenderer().renderModern(stack,false,-1);return;}if(owned){HeldEquipment.thirdPerson(p,stack,-1,Offhands.using(p));return;}LegacyHeldItem.third(p,stack);}
  finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }
 public static void slotBackground(int x,int y){
  Minecraft mc=Minecraft.getMinecraft();mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/gui/offhand_inventory.png"));GL11.glColor4f(1,1,1,1);net.minecraft.client.gui.Gui.func_146110_a(x,y,76,61,18,18,256,256);
  if(mc.thePlayer!=null&&Offhands.get(mc.thePlayer)==null){mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("moderncombatmovement:textures/gui/empty_offhand.png"));net.minecraft.client.gui.Gui.func_146110_a(x+1,y+1,0,0,16,16,16,16);}
 }
 private static float swing(EntityPlayer p,float partial){int ticks=Offhands.inventory(p).swingTicks;return ticks<=0?0:Math.min(1,(6-ticks+partial)/6F);}
 public static void firstPerson(float partial){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer p=mc.thePlayer;ItemStack stack=p==renderOwner?rendered:p==null?null:Offhands.get(p);if(stack==null||p.isInvisible())return;
  ItemStack activeStack=((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack(),main=com.glowingfederal.moderncombatmovement.combat.CombatHooks.held(p);
  if(!Offhands.using(p)&&(activeStack!=null&&(activeStack.getItem() instanceof net.minecraft.item.ItemBow||com.glowingfederal.moderncombatmovement.compat.Crossbows.known(activeStack))||activeStack==null&&com.glowingfederal.moderncombatmovement.compat.Crossbows.known(main)&&com.glowingfederal.moderncombatmovement.compat.Crossbows.charged(main)))return;
  if(Offhands.using(p)&&stack.getItem() instanceof net.minecraft.item.ItemBow){
   GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();try{net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();BowHandTransforms.use(HandMatrices.INSTANCE,-1,1-(previousEquip+(equip-previousEquip)*partial),p.getItemInUseDuration()+partial-1);HeldBow.render(stack,p,-1);}finally{GL11.glPopMatrix();GL11.glPopAttrib();}return;
  }
  if(com.glowingfederal.moderncombatmovement.compat.Shields.known(stack)||com.glowingfederal.moderncombatmovement.compat.Tridents.known(stack)){
   HeldEquipment.firstPerson(p,stack,-1,1-(previousEquip+(equip-previousEquip)*partial),swing(p,partial),Offhands.using(p)&&((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack()==stack,partial);return;
  }
  if(com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)!=null||com.glowingfederal.moderncombatmovement.compat.Crossbows.known(stack)){
   GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_LIGHTING_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();try{
    net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();boolean active=Offhands.using(p)&&((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack()==stack;float unequip=1-(previousEquip+(equip-previousEquip)*partial);
    if(com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack)!=null){SpearAnimationMath.firstPerson(HandMatrices.INSTANCE,com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spear(stack),-1,unequip,swing(p,partial),active,p.getItemInUseDuration()+partial-1,com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).kineticHitTicks+partial);new SpearItemRenderer().renderModern(stack,true,-1);}
    else{CrossbowHandTransforms.apply(HandMatrices.INSTANCE,-1,unequip,swing(p,partial),active,p.getItemInUseDuration()+partial-1,com.glowingfederal.moderncombatmovement.compat.Crossbows.chargeTime(stack),com.glowingfederal.moderncombatmovement.compat.Crossbows.charged(stack),false);GL11.glScalef(-1,1,1);GL11.glDisable(GL11.GL_CULL_FACE);new CrossbowItemRenderer().renderFirstPersonModern(stack,p);}
   }finally{GL11.glPopMatrix();GL11.glPopAttrib();}return;
  }
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT|GL11.GL_CURRENT_BIT|GL11.GL_COLOR_BUFFER_BIT);GL11.glPushMatrix();
  try{net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();GL11.glTranslatef(-.56F,-.52F-(1-(previousEquip+(equip-previousEquip)*partial))*.6F,-.72F);if(Offhands.using(p)){GL11.glTranslatef(.14F,.12F,-.10F);GL11.glRotatef(-25,0,1,0);}float swing=swing(p,partial);if(swing>0){float root=(float)Math.sqrt(swing);GL11.glTranslatef((float)Math.sin(root*Math.PI)*.4F,(float)Math.sin(root*Math.PI*2)*.2F,-(float)Math.sin(swing*Math.PI)*.2F);GL11.glRotatef((float)Math.sin(root*Math.PI)*20,0,0,1);}
   GL11.glRotatef(45,0,1,0);GL11.glScalef(.5F,.5F,.5F);LegacyHeldItem.draw(p,stack);}
  finally{GL11.glPopMatrix();GL11.glPopAttrib();}
 }
}
