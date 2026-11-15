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
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.BoostRocket.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball(manager,net.minecraft.init.Items.FIREWORKS,net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
  net.minecraft.item.Item wind=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge");if(wind!=null)net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(wind,0,new net.minecraft.client.renderer.block.model.ModelResourceLocation("moderncombatmovement:wind_charge","inventory"));net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.WindChargeProjectile.class,manager->new net.minecraft.client.renderer.entity.RenderSnowball(manager,com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge")!=null?com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("wind_charge"):net.minecraft.init.Items.SNOWBALL,net.minecraft.client.Minecraft.getMinecraft().getRenderItem()));
  net.minecraft.item.Item standaloneTrident=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("trident");if(standaloneTrident!=null)net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(standaloneTrident,0,new net.minecraft.client.renderer.block.model.ModelResourceLocation("moderncombatmovement:trident","inventory"));
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.TridentProjectile.class,manager->new TridentRenderer(manager));net.minecraft.item.Item trident=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("trident");if(trident!=null)trident.setTileEntityItemStackRenderer(new TridentStackRenderer()); SpearModels.init();
  net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt.class,manager->new net.minecraft.client.renderer.entity.RenderArrow<com.glowingfederal.moderncombatmovement.compat.CrossbowBolt>(manager) { protected net.minecraft.util.ResourceLocation getEntityTexture(com.glowingfederal.moderncombatmovement.compat.CrossbowBolt bolt) { return new net.minecraft.util.ResourceLocation("textures/entity/projectiles/arrow.png"); } });
  if(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone!=null) net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(net.minecraft.item.Item.getItemFromBlock(com.glowingfederal.moderncombatmovement.compat.Anchors.standalone),0,new net.minecraft.client.renderer.block.model.ModelResourceLocation("moderncombatmovement:respawn_anchor","inventory"));
 for(java.util.Map.Entry<String,net.minecraft.item.Item> entry : com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.entrySet()) {
   net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(entry.getValue(),0,new net.minecraft.client.renderer.block.model.ModelResourceLocation("moderncombatmovement:"+entry.getKey(),"inventory"));
  } net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(this); }
 @SubscribeEvent public void tridentModel(net.minecraftforge.client.event.ModelBakeEvent event){
  net.minecraft.item.Item item=com.glowingfederal.moderncombatmovement.compat.Content.ITEMS.get("trident");if(item==null)return;
  try{net.minecraftforge.client.model.IModel hand=net.minecraftforge.client.model.ModelLoaderRegistry.getModel(new net.minecraft.util.ResourceLocation("moderncombatmovement:item/trident_in_hand"));
   net.minecraft.client.renderer.block.model.IBakedModel held=hand.bake(hand.getDefaultState(),net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM,location->Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
   net.minecraft.client.renderer.block.model.ModelResourceLocation id=new net.minecraft.client.renderer.block.model.ModelResourceLocation(item.getRegistryName(),"inventory");event.getModelRegistry().putObject(id,new HeldTridentModel(event.getModelRegistry().getObject(id),held));
  }catch(Exception failure){throw new IllegalStateException("Could not bake trident hand model",failure);}
 }
 public void receive(Packets.Snapshot packet) { if(pending.size()<4096 || pending.containsKey(packet.uuid)) pending.put(packet.uuid,packet); }
 public void fireworks(net.minecraft.world.World world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.nbt.NBTTagCompound data){world.makeFireworks(x,y,z,vx,vy,vz,data);}
 public boolean local(EntityPlayer player) { return player==Minecraft.getMinecraft().player; }
 @SubscribeEvent public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) { disconnected=true; }
 @SubscribeEvent public void tick(TickEvent.ClientTickEvent e) {
  if(e.phase!=TickEvent.Phase.START) return;
  Minecraft mc=Minecraft.getMinecraft();
  if(disconnected || mc.world==null) { pending.clear(); Settings.resetRemote(); disconnected=false; return; }
  for(Packets.Snapshot packet : pending.values()) {
   if(packet.dimension!=mc.world.provider.getDimension()) { pending.remove(packet.uuid,packet); continue; }
   Entity entity=mc.world.getEntityByID(packet.id);
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
  boolean eyes=p.isInsideOfMaterial(net.minecraft.block.material.Material.WATER);
  boolean using=p.isHandActive() && !com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spearUsing(p);
  boolean possible=(p.getFoodStats().getFoodLevel()>6 || p.capabilities.allowFlying) && !p.isPotionActive(net.minecraft.init.MobEffects.BLINDNESS);
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
   else if(!possible || !forward || p.collidedHorizontally && !minor || p.isInWater() && !eyes && !p.capabilities.isFlying) running=false;
  }
  if(p.movementInput.sneak || using || !forward) s.sprintWindow=0;
  s.wasForward=forward; s.sprinting=running; p.setSprinting(running);
  s.jump=p.movementInput.jump;
  if(p.isInWater() && p.movementInput.sneak && !p.capabilities.isFlying) p.motionY-=.04F;
  if(lastJump!=s.jump || p.ticksExisted%20==0) { Packets.channel.sendToServer(new Packets.Intent(s.jump)); lastJump=s.jump; }
 }
}
