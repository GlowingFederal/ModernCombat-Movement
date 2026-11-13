package com.glowingfederal.moderncombatmovement.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.*;
import net.minecraft.world.WorldServer;
import com.glowingfederal.moderncombatmovement.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class Packets {
 private static final ConcurrentHashMap<UUID,Boolean> FLIGHTS=new ConcurrentHashMap<UUID,Boolean>();
 private static final ConcurrentHashMap<UUID,Boolean> JABS=new ConcurrentHashMap<UUID,Boolean>();
 private static final ConcurrentHashMap<UUID,Boolean> INPUT=new ConcurrentHashMap<UUID,Boolean>();
 public static SimpleNetworkWrapper channel;
 private Packets() {}
 public static void init() {
  channel=NetworkRegistry.INSTANCE.newSimpleChannel("mcm_state");
  channel.registerMessage(IntentHandler.class,Intent.class,0,Side.SERVER);
  channel.registerMessage(SnapshotHandler.class,Snapshot.class,1,Side.CLIENT);
  channel.registerMessage(JabHandler.class,Jab.class,2,Side.SERVER);
  channel.registerMessage(FlightHandler.class,Flight.class,3,Side.SERVER);
  cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new Events());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void consume(EntityPlayer p) {
  if(FLIGHTS.remove(p.getUniqueID())!=null&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.raw(p)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))com.glowingfederal.moderncombatmovement.movement.FlightHooks.stop(p);
  if(JABS.remove(p.getUniqueID())!=null)com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);
  Boolean jump=INPUT.remove(p.getUniqueID());
  if(jump!=null) PlayerHooks.state(p).jump=jump.booleanValue();
 }
 public static void publish(EntityPlayer p) {
  PlayerState s=PlayerHooks.state(p); int bits=s.pose.ordinal() | (s.swimming ? 4 : 0) | (Settings.localPolicy<<3);
  if(bits==s.lastSent && p.ticksExisted%20!=0) return;
  s.lastSent=bits;
  Snapshot packet=new Snapshot(p);
  ((WorldServer)p.worldObj).getEntityTracker().func_151247_a(p,channel.getPacketFrom(packet));
  channel.sendTo(packet,(EntityPlayerMP)p);
 }
 public static final class Events {
  @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if(e.player instanceof EntityPlayerMP) channel.sendTo(new Snapshot(e.player),(EntityPlayerMP)e.player); }
  @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e) { INPUT.remove(e.player.getUniqueID());JABS.remove(e.player.getUniqueID());FLIGHTS.remove(e.player.getUniqueID()); }
  @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e) {
   if(e.target instanceof EntityPlayer && e.entityPlayer instanceof EntityPlayerMP) channel.sendTo(new Snapshot((EntityPlayer)e.target),(EntityPlayerMP)e.entityPlayer);
  }
 }
 public static final class Flight implements IMessage {public void fromBytes(ByteBuf b){}public void toBytes(ByteBuf b){}}
 public static final class FlightHandler implements IMessageHandler<Flight,IMessage>{public IMessage onMessage(Flight m,MessageContext ctx){FLIGHTS.put(ctx.getServerHandler().playerEntity.getUniqueID(),Boolean.TRUE);return null;}}
 public static final class Jab implements IMessage {public void fromBytes(ByteBuf b){}public void toBytes(ByteBuf b){}}
 public static final class JabHandler implements IMessageHandler<Jab,IMessage>{public IMessage onMessage(Jab m,MessageContext ctx){JABS.put(ctx.getServerHandler().playerEntity.getUniqueID(),Boolean.TRUE);return null;}}
 public static final class Intent implements IMessage {
  public boolean jump;
  public Intent() {} public Intent(boolean jump) { this.jump=jump; }
  public void fromBytes(ByteBuf b) { jump=b.readBoolean(); }
  public void toBytes(ByteBuf b) { b.writeBoolean(jump); }
 }
 public static final class IntentHandler implements IMessageHandler<Intent,IMessage> {
  public IMessage onMessage(Intent m,MessageContext ctx) {
   // Network thread: coalesce only. No entity/world mutations or unbounded runnable queues.
   INPUT.put(ctx.getServerHandler().playerEntity.getUniqueID(),Boolean.valueOf(m.jump)); return null;
  }
 }
 public static final class Snapshot implements IMessage {
  public int dimension,id,policy; public UUID uuid; public Pose pose; public boolean swimming;
  public Snapshot() {}
  public Snapshot(EntityPlayer p) { dimension=p.dimension; id=p.getEntityId(); uuid=p.getUniqueID(); policy=Settings.localPolicy; pose=PlayerHooks.state(p).pose; swimming=PlayerHooks.state(p).swimming; }
  public void fromBytes(ByteBuf b) { dimension=b.readInt(); id=b.readInt(); uuid=new UUID(b.readLong(),b.readLong()); policy=b.readUnsignedByte(); int index=b.readUnsignedByte(); if(index>=Pose.values().length || (policy&~127)!=0) throw new IllegalArgumentException("Invalid MCM snapshot"); pose=Pose.values()[index]; swimming=b.readBoolean(); }
  public void toBytes(ByteBuf b) { b.writeInt(dimension); b.writeInt(id); b.writeLong(uuid.getMostSignificantBits()); b.writeLong(uuid.getLeastSignificantBits()); b.writeByte(policy); b.writeByte(pose.ordinal()); b.writeBoolean(swimming); }
 }
 public static final class SnapshotHandler implements IMessageHandler<Snapshot,IMessage> {
  public IMessage onMessage(Snapshot m,MessageContext ctx) { ModernCombatMovement.proxy.receive(m); return null; }
 }
}
