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
  ClientOptionsEvents optionsEvents=new ClientOptionsEvents();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(optionsEvents);net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(optionsEvents);
  TotemActivation.init();
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.RespawnedDragon.class,manager->new net.minecraft.client.renderer.entity.RenderDragon(manager));
  if(!com.glowingfederal.moderncombatmovement.compat.Content.uab){GlowingEffectRenderer outlines=new GlowingEffectRenderer();net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(outlines);net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(outlines);}
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.LingeringCloud.class,new LingeringCloudRenderer(net.minecraft.client.Minecraft.getMinecraft().getRenderManager()));
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.LingeringProjectile.class,new LingeringPotionRenderer(net.minecraft.client.Minecraft.getMinecraft().getRenderManager()));

  net.minecraft.item.Item wind=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge");if(wind!=null)net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(wind,0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:wind_charge","inventory"));net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.WindChargeProjectile.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball(manager,com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge")!=null?com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge"):net.minecraft.init.Items.snowball,net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
  net.minecraft.item.Item standaloneTrident=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("trident");if(standaloneTrident!=null)net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(standaloneTrident,0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:trident","inventory"));
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.TridentProjectile.class,manager->new TridentRenderer(manager));
  OffhandClient.init();
  net.minecraft.item.Item shield=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("shield");if(shield!=null)net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(shield,0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:shield","inventory"));
  final net.minecraft.item.Item glider=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("elytra");
  if(glider!=null){final net.minecraft.client.resources.model.ModelResourceLocation intact=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:elytra","inventory"),broken=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:elytra_broken","inventory");net.minecraft.client.resources.model.ModelBakery.registerItemVariants(glider,new net.minecraft.util.ResourceLocation("moderncombatmovement:elytra"),new net.minecraft.util.ResourceLocation("moderncombatmovement:elytra_broken"));net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(glider,stack->stack.getItemDamage()>=stack.getMaxDamage()-1?broken:intact);}

  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.BoostRocket.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball(manager,net.minecraft.init.Items.fireworks,net.minecraft.client.Minecraft.getMinecraft().getRenderItem())); SpearModels.init();
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.PlacedCrystal.class,manager->new net.minecraft.client.renderer.tileentity.RenderEnderCrystal(manager));
  final net.minecraft.item.Item crossbow=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("crossbow");
  if(crossbow!=null) {
   final net.minecraft.client.resources.model.ModelResourceLocation[] models=new net.minecraft.client.resources.model.ModelResourceLocation[6];
   String[] names={"crossbow","crossbow_pulling_0","crossbow_pulling_1","crossbow_pulling_2","crossbow_arrow","crossbow_firework"};
   for(int i=0;i<names.length;i++)models[i]=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:"+names[i],"inventory");
   net.minecraft.util.ResourceLocation[] variants=new net.minecraft.util.ResourceLocation[names.length];
   for(int i=0;i<names.length;i++)variants[i]=new net.minecraft.util.ResourceLocation("moderncombatmovement",names[i]);
   net.minecraft.client.resources.model.ModelBakery.registerItemVariants(crossbow,variants);
   net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(crossbow,stack->{
    if(com.glowingfederal.moderncombatmovement.compat.Crossbows.charged(stack))return models[com.glowingfederal.moderncombatmovement.compat.Crossbows.firework(stack)?5:4];
    EntityPlayer p=Minecraft.getMinecraft().thePlayer;
    if(p!=null && p.getItemInUse()==stack) { float progress=(float)p.getItemInUseDuration()/com.glowingfederal.moderncombatmovement.compat.Crossbows.chargeTime(stack);return models[progress>=1?3:progress>=.58F?2:1]; }
    return models[0];
   });
  }

  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt.class,new AmmunitionRenderer(net.minecraft.client.Minecraft.getMinecraft().getRenderManager()));
  if(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone!=null) net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone),0,new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:respawn_anchor","inventory"));
 final net.minecraft.item.Item tipped=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("tipped_arrow");
 if(tipped!=null){final net.minecraft.client.resources.model.ModelResourceLocation arrowModel=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:tipped_arrow","inventory");net.minecraft.client.resources.model.ModelBakery.registerItemVariants(tipped,new net.minecraft.util.ResourceLocation("moderncombatmovement:tipped_arrow"));net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(tipped,stack->arrowModel);}
 final net.minecraft.item.Item lingering=com.glowingfederal.moderncombatmovement.compat.PotionDelivery.lingering;
 if(lingering!=null&&!com.glowingfederal.moderncombatmovement.compat.Content.uab){
  final net.minecraft.client.resources.model.ModelResourceLocation model=new net.minecraft.client.resources.model.ModelResourceLocation("moderncombatmovement:lingering_potion","inventory");
  net.minecraft.client.resources.model.ModelBakery.registerItemVariants(lingering,new net.minecraft.util.ResourceLocation("moderncombatmovement:lingering_potion"));
  net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(lingering,stack->model);
 }
 for(java.util.Map.Entry<String,net.minecraft.item.Item> entry : com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.entrySet()) {
   if(entry.getValue()==crossbow||entry.getValue()==glider||entry.getValue()==lingering||entry.getValue()==tipped)continue;
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
  if(disconnected || mc.theWorld==null) { ClientControls.reset();lastJump=false;OffhandClient.reset();feedback.clear();pending.clear(); Settings.resetRemote(); disconnected=false; return; }
  ClientControls.tick();
  boolean gliding=mc.thePlayer!=null&&com.glowingfederal.moderncombatmovement.movement.FlightHooks.flying(mc.thePlayer);
  if(gliding&&(!wasGliding||soundPlayer!=mc.thePlayer)&&!com.glowingfederal.moderncombatmovement.compat.Content.uab)mc.getSoundHandler().playSound(new FlightSound(mc.thePlayer));
  soundPlayer=mc.thePlayer;wasGliding=gliding;
  com.glowingfederal.moderncombatmovement.render.OffhandClient.tick();
  for(int count=0;count<128;count++){Packets.Feedback event=feedback.poll();if(event==null)break;if(mc.thePlayer!=null&&event.dimension==mc.thePlayer.dimension&&event.recipient.equals(mc.thePlayer.getUniqueID()))CombatFeedback.apply(event);}
  for(Packets.Snapshot packet : pending.values()) {
   if(packet.dimension!=mc.theWorld.provider.getDimensionId()) { pending.remove(packet.uuid,packet); continue; }
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
  
 }
}
