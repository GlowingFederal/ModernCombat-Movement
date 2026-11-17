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
 private final java.util.concurrent.ConcurrentLinkedQueue<Packets.Feedback> feedback=new java.util.concurrent.ConcurrentLinkedQueue<Packets.Feedback>();
 public void windTrail(net.minecraft.world.World world,double x,double y,double z){Minecraft.getMinecraft().effectRenderer.addEffect(new GustParticle(world,x,y,z,true));}
 public void feedback(Packets.Feedback packet){if(feedback.size()<1024)feedback.offer(packet);}
 public static float framePartial;
 @SubscribeEvent public void frame(TickEvent.RenderTickEvent e){framePartial=e.renderTickTime;}
 private boolean lastJump;
 private net.minecraft.entity.player.EntityPlayer soundPlayer;private boolean wasGliding;
 public void init() {
  if(!com.glowingfederal.moderncombatmovement.compat.Content.uab)net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new EquipmentSounds());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new GustParticle.Textures());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new CombatHud());
  ClientOptionsEvents optionsEvents=new ClientOptionsEvents();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(optionsEvents);cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(optionsEvents);
  TotemActivation.init();
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new CrossbowIcons());
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.RespawnedDragon.class,new net.minecraft.client.renderer.entity.RenderDragon());
  if(!com.glowingfederal.moderncombatmovement.compat.Content.uab){GlowingEffectRenderer outlines=new GlowingEffectRenderer();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(outlines);cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(outlines);}
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.LingeringCloud.class,new LingeringCloudRenderer());
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.LingeringProjectile.class,new LingeringPotionRenderer());

  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.WindChargeProjectile.class,new net.minecraft.client.renderer.entity.RenderSnowball(cpw.mods.fml.common.registry.GameRegistry.findItem(com.glowingfederal.moderncombatmovement.compat.Content.uab?"etfuturum":"moderncombatmovement","wind_charge")));
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.TridentProjectile.class,new TridentRenderer());
  OffhandClient.init();
  net.minecraft.item.Item shield=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("shield");if(shield!=null)net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(shield,new ShieldItemRenderer());
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.BoostRocket.class,new net.minecraft.client.renderer.entity.RenderSnowball(net.minecraft.init.Items.fireworks));
  SpearItemRenderer spearRenderer=new SpearItemRenderer();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(spearRenderer);
  for(com.glowingfederal.moderncombatmovement.combat.SpearSpec spec:com.glowingfederal.moderncombatmovement.combat.SpearSpec.values()){net.minecraft.item.Item item=cpw.mods.fml.common.registry.GameRegistry.findItem(com.glowingfederal.moderncombatmovement.compat.Content.uab?"etfuturum":"moderncombatmovement",spec.material+"_spear");if(item!=null)net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(item,spearRenderer);}

  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.PlacedCrystal.class,new net.minecraft.client.renderer.tileentity.RenderEnderCrystal());
  net.minecraft.item.Item crossbow=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("crossbow");
  if(crossbow!=null) net.minecraftforge.client.MinecraftForgeClient.registerItemRenderer(crossbow,new CrossbowItemRenderer());
  cpw.mods.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt.class,new AmmunitionRenderer()); cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(this); }
 public void fireworks(net.minecraft.world.World w,double x,double y,double z,double vx,double vy,double vz,net.minecraft.nbt.NBTTagCompound data){w.makeFireworks(x,y,z,vx,vy,vz,data);}
 public void receive(Packets.Snapshot packet) { if(pending.size()<4096 || pending.containsKey(packet.uuid)) pending.put(packet.uuid,packet); }
 public boolean local(EntityPlayer player) { return player==Minecraft.getMinecraft().thePlayer; }
 @SubscribeEvent public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) { disconnected=true; }
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e) {
  if(e.phase!=TickEvent.Phase.START) return;
  Minecraft mc=Minecraft.getMinecraft();
  if(disconnected || mc.theWorld==null) { ClientControls.reset();lastJump=false;OffhandClient.reset();feedback.clear();pending.clear(); Settings.resetRemote(); disconnected=false; return; }
  ClientControls.tick();
  boolean gliding=mc.thePlayer!=null&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(mc.thePlayer);
  if(gliding&&(!wasGliding||soundPlayer!=mc.thePlayer)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.uabFlight)mc.getSoundHandler().playSound(new FlightSound(mc.thePlayer));
  soundPlayer=mc.thePlayer;wasGliding=gliding;
  com.glowingfederal.moderncombatmovement.render.OffhandClient.tick();
  for(int count=0;count<128;count++){Packets.Feedback event=feedback.poll();if(event==null)break;if(mc.thePlayer!=null&&event.dimension==mc.thePlayer.dimension&&event.recipient.equals(mc.thePlayer.getUniqueID()))CombatFeedback.apply(event);}
  for(Packets.Snapshot packet : pending.values()) {
   if(packet.dimension!=mc.theWorld.provider.dimensionId) { pending.remove(packet.uuid,packet); continue; }
   Entity entity=mc.theWorld.getEntityByID(packet.id);
   if(!(entity instanceof EntityPlayer) || !entity.getUniqueID().equals(packet.uuid)) continue;
   EntityPlayer p=(EntityPlayer)entity;
   if(!com.glowingfederal.moderncombatmovement.network.RegistryAgreement.signature().equals(packet.registry)){
    pending.clear();mc.getNetHandler().getNetworkManager().closeChannel(new net.minecraft.util.ChatComponentText("Modern Combat & Movement: client/server enchantment or effect IDs differ. Match the installed UAB version and MCM Compatibility ID settings. Server: "+packet.registry+" Client: "+com.glowingfederal.moderncombatmovement.network.RegistryAgreement.signature()));return;
   }
   Settings.acceptPolicy(packet.policy);
   com.glowingfederal.moderncombatmovement.config.ClientOptions.serverFlightLimit=packet.flightLimit;
   com.glowingfederal.moderncombatmovement.render.OffhandClient.apply(p,packet);
   PlayerState s=PlayerHooks.state(p); s.kineticHitTicks=packet.kineticHitTicks;s.pose=packet.pose; s.swimming=packet.swimming;s.spinTicks=packet.spinTicks;
   if(PlayerHooks.active(p)||packet.spinTicks>0) PlayerHooks.dimensions(p);
   pending.remove(packet.uuid,packet);
  }
 }
 public void input(EntityPlayer player) {
  EntityPlayerSP p=(EntityPlayerSP)player; Minecraft mc=Minecraft.getMinecraft(); PlayerState s=PlayerHooks.state(p);
  if(!Settings.movement(true)) return;
  boolean jumpNow=p.movementInput.jump;if(jumpNow&&!s.flightJump&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))Packets.channel.sendToServer(new Packets.Flight());s.flightJump=jumpNow;
  s.jump=p.movementInput.jump;
  if(lastJump!=s.jump || p.ticksExisted%20==0) { Packets.channel.sendToServer(new Packets.Intent(s.jump)); lastJump=s.jump; }
  if(com.glowingfederal.moderncombatmovement.compat.MovementCompat.owns(p)||ClientControls.external())return;
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
  
 }
}
