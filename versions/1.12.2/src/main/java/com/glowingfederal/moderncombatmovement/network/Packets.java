package com.glowingfederal.moderncombatmovement.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.*;
import net.minecraft.world.WorldServer;
import com.glowingfederal.moderncombatmovement.*;
import com.glowingfederal.moderncombatmovement.pose.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class Packets {
 private static final ConcurrentHashMap<UUID,Boolean> INPUT=new ConcurrentHashMap<UUID,Boolean>();
 public static SimpleNetworkWrapper channel;
 private Packets() {}
 public static void init() {
  channel=NetworkRegistry.INSTANCE.newSimpleChannel("mcm_state");
  channel.registerMessage(IntentHandler.class,Intent.class,0,Side.SERVER);
  channel.registerMessage(SnapshotHandler.class,Snapshot.class,1,Side.CLIENT);
  net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(new Events());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void consume(EntityPlayer p) {
  Boolean jump=INPUT.remove(p.getUniqueID());
  if(jump!=null) PlayerHooks.state(p).jump=jump.booleanValue();
 }
 public static void publish(EntityPlayer p) {
  PlayerState s=PlayerHooks.state(p); int bits=s.pose.ordinal() | (s.swimming ? 4 : 0) | (Settings.localPolicy<<3);
  if(bits==s.lastSent && p.ticksExisted%20!=0) return;
  s.lastSent=bits;
  Snapshot packet=new Snapshot(p);
  ((WorldServer)p.world).getEntityTracker().sendToTracking(p,channel.getPacketFrom(packet));
  channel.sendTo(packet,(EntityPlayerMP)p);
 }
 public static final class Events {
  @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if(e.player instanceof EntityPlayerMP) channel.sendTo(new Snapshot(e.player),(EntityPlayerMP)e.player); }
  @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e) { INPUT.remove(e.player.getUniqueID()); }
  @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e) {
   if(e.getTarget() instanceof EntityPlayer && e.getEntityPlayer() instanceof EntityPlayerMP) channel.sendTo(new Snapshot((EntityPlayer)e.getTarget()),(EntityPlayerMP)e.getEntityPlayer());
  }
 }
 public static final class Intent implements IMessage {
  public boolean jump;
  public Intent() {} public Intent(boolean jump) { this.jump=jump; }
  public void fromBytes(ByteBuf b) { jump=b.readBoolean(); }
  public void toBytes(ByteBuf b) { b.writeBoolean(jump); }
 }
 public static final class IntentHandler implements IMessageHandler<Intent,IMessage> {
  public IMessage onMessage(Intent m,MessageContext ctx) {
   // Network thread: coalesce only. No entity/world mutations or unbounded runnable queues.
   INPUT.put(ctx.getServerHandler().player.getUniqueID(),Boolean.valueOf(m.jump)); return null;
  }
 }
 public static final class Snapshot implements IMessage {
  public int dimension,id,policy; public UUID uuid; public Pose pose; public boolean swimming;
  public Snapshot() {}
  public Snapshot(EntityPlayer p) { dimension=p.dimension; id=p.getEntityId(); uuid=p.getUniqueID(); policy=Settings.localPolicy; pose=PlayerHooks.state(p).pose; swimming=PlayerHooks.state(p).swimming; }
  public void fromBytes(ByteBuf b) { dimension=b.readInt(); id=b.readInt(); uuid=new UUID(b.readLong(),b.readLong()); policy=b.readUnsignedByte(); int index=b.readUnsignedByte(); if(index>=Pose.values().length || (policy&~31)!=0) throw new IllegalArgumentException("Invalid MCM snapshot"); pose=Pose.values()[index]; swimming=b.readBoolean(); }
  public void toBytes(ByteBuf b) { b.writeInt(dimension); b.writeInt(id); b.writeLong(uuid.getMostSignificantBits()); b.writeLong(uuid.getLeastSignificantBits()); b.writeByte(policy); b.writeByte(pose.ordinal()); b.writeBoolean(swimming); }
 }
 public static final class SnapshotHandler implements IMessageHandler<Snapshot,IMessage> {
  public IMessage onMessage(Snapshot m,MessageContext ctx) { ModernCombatMovement.proxy.receive(m); return null; }
 }
}
