package com.glowingfederal.moderncombatmovement.render;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
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
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.BoostRocket.class,new net.minecraft.client.renderer.entity.RenderSnowball(net.minecraft.init.Items.fireworks));
  SpearItemRenderer spearRenderer=new SpearItemRenderer();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(spearRenderer);
  for(com.glowingfederal.moderncombatmovement.combat.SpearSpec spec:com.glowingfederal.moderncombatmovement.combat.SpearSpec.values()){net.minecraft.item.Item item=cpw.mods.fml.common.registry.GameRegistry.findItem(com.glowingfederal.moderncombatmovement.compat.Content.uab?"etfuturum":"moderncombatmovement",spec.material+"_spear");if(item!=null)net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(item,spearRenderer);}

  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.PlacedCrystal.class,new net.minecraft.client.renderer.tileentity.RenderEnderCrystal());
  net.minecraft.item.Item crossbow=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("crossbow");
  if(crossbow!=null) net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(crossbow,new CrossbowItemRenderer());
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt.class,new net.minecraft.client.renderer.entity.RenderArrow()); cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(this); }
 public void fireworks(net.minecraft.world.World w,double x,double y,double z,double vx,double vy,double vz,net.minecraft.nbt.NBTTagCompound data){w.makeFireworks(x,y,z,vx,vy,vz,data);}
 public void receive(Packets.Snapshot packet) { if(pending.size()<4096 || pending.containsKey(packet.uuid)) pending.put(packet.uuid,packet); }
 public boolean local(EntityPlayer player) { return player==Minecraft.getMinecraft().thePlayer; }
 @SubscribeEvent public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) { disconnected=true; }
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e) {
  if(e.phase!=TickEvent.Phase.START) return;
  Minecraft mc=Minecraft.getMinecraft();
  if(disconnected || mc.theWorld==null) { pending.clear(); Settings.resetRemote(); disconnected=false; return; }
  for(Packets.Snapshot packet : pending.values()) {
   if(packet.dimension!=mc.theWorld.provider.dimensionId) { pending.remove(packet.uuid,packet); continue; }
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
   if(mc.gameSettings.keyBindSprint.getIsKeyPressed() || !s.wasForward && s.sprintWindow>0) running=true;
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
