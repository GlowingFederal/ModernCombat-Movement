package com.glowingfederal.moderncombatmovement.render;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.network.Packets;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.movement.MovementMath;
public final class ClientProxy extends CommonProxy {
 private final ConcurrentHashMap<UUID,Packets.Snapshot> pending=new ConcurrentHashMap<UUID,Packets.Snapshot>();
 private volatile boolean disconnected;
 private boolean lastJump;
 public void init() {
  final net.minecraft.item.Item glider=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("elytra");
  if(glider!=null){final net.minecraft.client.resources.model.ModelResourceLocation intact=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:elytra","inventory"),broken=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:elytra_broken","inventory");net.minecraft.client.resources.model.ModelBakery.registerItemVariants(glider,new net.minecraft.util.ResourceLocation("moderncombatmovement:elytra"),new net.minecraft.util.ResourceLocation("moderncombatmovement:elytra_broken"));net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(glider,stack->stack.getItemDamage()>=stack.getMaxDamage()-1?broken:intact);}

  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.BoostRocket.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball(manager,net.minecraft.init.Items.fireworks,net.minecraft.client.Minecraft.getMinecraft().getRenderItem())); SpearModels.init();
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.PlacedCrystal.class,manager->new net.minecraft.client.renderer.tileentity.RenderEnderCrystal(manager));
  final net.minecraft.item.Item crossbow=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("crossbow");
  if(crossbow!=null) {
   final net.minecraft.client.resources.model.ModelResourceLocation[] models=new net.minecraft.client.resources.model.ModelResourceLocation[5];
   String[] names={"crossbow","crossbow_pulling_0","crossbow_pulling_1","crossbow_pulling_2","crossbow_arrow"};
   for(int i=0;i<names.length;i++)models[i]=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:"+names[i],"inventory");
   net.minecraft.util.ResourceLocation[] variants=new net.minecraft.util.ResourceLocation[names.length];
   for(int i=0;i<names.length;i++)variants[i]=new net.minecraft.util.ResourceLocation("moderncombatmovement",names[i]);
   net.minecraft.client.resources.model.ModelBakery.registerItemVariants(crossbow,variants);
   net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(crossbow,stack->{
    if(com.glowingfederal.moderncombatmovement.compat.Crossbows.charged(stack))return models[4];
    EntityPlayer p=Minecraft.getMinecraft().thePlayer;
    if(p!=null && p.getItemInUse()==stack) { float progress=(float)p.getItemInUseDuration()/com.glowingfederal.moderncombatmovement.compat.Crossbows.chargeTime(stack);return models[progress>=1?3:progress>=.58F?2:1]; }
    return models[0];
   });
  }

  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt.class,new net.minecraft.client.renderer.entity.RenderArrow(net.minecraft.client.Minecraft.getMinecraft().getRenderManager()));
  if(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone!=null) net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone),0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:respawn_anchor","inventory"));
 for(java.util.Map.Entry<String,net.minecraft.item.Item> entry : com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.entrySet()) {
   if(entry.getValue()==crossbow||entry.getValue()==glider)continue;
   net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(entry.getValue(),0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:"+entry.getKey(),"inventory"));
  } net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(this); }
 public void fireworks(net.minecraft.world.World w,double x,double y,double z,double vx,double vy,double vz,net.minecraft.nbt.NBTTagCompound data){w.makeFireworks(x,y,z,vx,vy,vz,data);}
 public void receive(Packets.Snapshot packet) { if(pending.size()<4096 || pending.containsKey(packet.uuid)) pending.put(packet.uuid,packet); }
 public boolean local(EntityPlayer player) { return player==Minecraft.getMinecraft().thePlayer; }
 @SubscribeEvent public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) { disconnected=true; }
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e) {
  if(e.phase!=TickEvent.Phase.START) return;
  ElytraLayers.install();
  Minecraft mc=Minecraft.getMinecraft();
  if(disconnected || mc.theWorld==null) { pending.clear(); Settings.resetRemote(); disconnected=false; return; }
  for(Packets.Snapshot packet : pending.values()) {
   if(packet.dimension!=mc.theWorld.provider.getDimensionId()) { pending.remove(packet.uuid,packet); continue; }
   Entity entity=mc.theWorld.getEntityByID(packet.id);
   if(!(entity instanceof EntityPlayer) || !entity.getUniqueID().equals(packet.uuid)) continue;
   EntityPlayer p=(EntityPlayer)entity;
   Settings.acceptPolicy(packet.policy);
   PlayerState s=PlayerHooks.state(p); s.pose=packet.pose; s.swimming=packet.swimming;
   if(PlayerHooks.active(p)) PlayerHooks.dimensions(p);
   pending.remove(packet.uuid,packet);
  }
 }
 public void input(EntityPlayer player) {
  EntityPlayerSP p=(EntityPlayerSP)player; Minecraft mc=Minecraft.getMinecraft(); PlayerState s=PlayerHooks.state(p);
  if(!Settings.movement(true)) return;
  boolean jumpNow=p.movementInput.jump;if(jumpNow&&!s.flightJump&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))Packets.channel.sendToServer(new Packets.Flight());s.flightJump=jumpNow;
  boolean forward=p.movementInput.moveForward>1.0E-5F;
  boolean eyes=p.isInsideOfMaterial(net.minecraft.block.material.Material.water);
  boolean using=p.isUsingItem() && !com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spearUsing(p);
  boolean possible=(p.getFoodStats().getFoodLevel()>6 || p.capabilities.allowFlying) && !p.isPotionActive(net.minecraft.potion.Potion.blindness);
  boolean slow=s.pose==Pose.CROUCHING || s.pose==Pose.LOW && !p.isInWater();
  double yaw=p.rotationYaw*Math.PI/180, x=p.movementInput.moveStrafe*Math.cos(yaw)-p.movementInput.moveForward*Math.sin(yaw), z=p.movementInput.moveForward*Math.cos(yaw)+p.movementInput.moveStrafe*Math.sin(yaw);
  boolean minor=MovementMath.minorCollision(x,z,p.posX-p.prevPosX,p.posZ-p.prevPosZ);
  boolean running=s.sprinting;
  if(s.sprintWindow>0) s.sprintWindow--;
  boolean canStart=possible && forward && !using && (!slow || eyes) && (!p.isInWater() || eyes || p.capabilities.isFlying);
  if(!running && canStart) {
   if(mc.gameSettings.keyBindSprint.isKeyDown() || !s.wasForward && s.sprintWindow>0) running=true;
   else if(!s.wasForward) s.sprintWindow=7;
  }
  if(running) {
   if(s.swimming) { if(!possible || !p.isInWater() || !forward && !p.onGround && !p.movementInput.sneak) running=false; }
   else if(!possible || !forward || p.isCollidedHorizontally && !minor || p.isInWater() && !eyes && !p.capabilities.isFlying) running=false;
  }
  if(p.movementInput.sneak || using || !forward) s.sprintWindow=0;
  s.wasForward=forward; s.sprinting=running; p.setSprinting(running);
  s.jump=p.movementInput.jump;
  if(p.isInWater() && p.movementInput.sneak && !p.capabilities.isFlying) p.motionY-=.04F;
  if(lastJump!=s.jump || p.ticksExisted%20==0) { Packets.channel.sendToServer(new Packets.Intent(s.jump)); lastJump=s.jump; }
 }
}
