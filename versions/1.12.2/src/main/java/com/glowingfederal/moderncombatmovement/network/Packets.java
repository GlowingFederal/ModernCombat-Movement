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
 private static final ConcurrentHashMap<UUID,Boolean> FLIGHTS=new ConcurrentHashMap<UUID,Boolean>();
 private static final ConcurrentHashMap<UUID,Boolean> JABS=new ConcurrentHashMap<UUID,Boolean>();
 private static final ConcurrentHashMap<UUID,Boolean> INPUT=new ConcurrentHashMap<UUID,Boolean>();
 private static final ConcurrentHashMap<UUID,CreativeBoost> BOOSTS=new ConcurrentHashMap<UUID,CreativeBoost>();
 public static SimpleNetworkWrapper channel;
 private Packets() {}
 public static void init() {
  channel=NetworkRegistry.INSTANCE.newSimpleChannel("mcm_state");
  channel.registerMessage(FeedbackHandler.class,Feedback.class,7,Side.CLIENT);
  channel.registerMessage(CreativeBoostHandler.class,CreativeBoost.class,6,Side.SERVER);
  channel.registerMessage(IntentHandler.class,Intent.class,0,Side.SERVER);
  channel.registerMessage(SnapshotHandler.class,Snapshot.class,1,Side.CLIENT);
  channel.registerMessage(JabHandler.class,Jab.class,2,Side.SERVER);
  channel.registerMessage(FlightHandler.class,Flight.class,3,Side.SERVER);
  net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(new Events());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void consume(EntityPlayer p) {
  CreativeBoost boost=BOOSTS.remove(p.getUniqueID());if(boost!=null&&boost.dimension==p.dimension){PlayerState state=PlayerHooks.state(p);if(boost.multiplier==1)state.flightBoostBlocked=false;state.requestedFlightBoost=Math.min(boost.multiplier,Settings.creativeFlightLimit);state.flightBoostLease=40;}
  if(FLIGHTS.remove(p.getUniqueID())!=null&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.raw(p)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))com.glowingfederal.moderncombatmovement.movement.FlightHooks.stop(p);
  if(JABS.remove(p.getUniqueID())!=null)com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);
  Boolean jump=INPUT.remove(p.getUniqueID());
  if(jump!=null) PlayerHooks.state(p).jump=jump.booleanValue();
 }
 public static void publish(EntityPlayer p) {
  if(!(p instanceof EntityPlayerMP))return;
  EntityPlayerMP recipient=(EntityPlayerMP)p;
  if(recipient.connection==null||recipient.connection.netManager==null||!recipient.connection.netManager.isChannelOpen())return;
  PlayerState s=PlayerHooks.state(p); int bits=s.pose.ordinal() | (s.swimming ? 8 : 0) | (Settings.localPolicy<<4);
  if(bits==s.lastSent && p.ticksExisted%20!=0) return;
  s.lastSent=bits;
  Snapshot packet=new Snapshot(p);
  ((WorldServer)p.world).getEntityTracker().sendToTracking(p,channel.getPacketFrom(packet));
  channel.sendTo(packet,(EntityPlayerMP)p);
 }
 public static final class Events {
  @SubscribeEvent public void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e) { com.glowingfederal.moderncombatmovement.movement.CreativeFlight.clone(e.getOriginal(),e.getEntityPlayer()); }
  @SubscribeEvent public void dimension(PlayerEvent.PlayerChangedDimensionEvent e) { BOOSTS.remove(e.player.getUniqueID());com.glowingfederal.moderncombatmovement.movement.CreativeFlight.restore(e.player,true); }
  @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if(e.player instanceof EntityPlayerMP) channel.sendTo(new Snapshot(e.player),(EntityPlayerMP)e.player); }
  @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e) { BOOSTS.remove(e.player.getUniqueID());com.glowingfederal.moderncombatmovement.movement.CreativeFlight.restore(e.player,false); INPUT.remove(e.player.getUniqueID());JABS.remove(e.player.getUniqueID());FLIGHTS.remove(e.player.getUniqueID()); }
  @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e) {
   if(e.getTarget() instanceof EntityPlayer && e.getEntityPlayer() instanceof EntityPlayerMP) channel.sendTo(new Snapshot((EntityPlayer)e.getTarget()),(EntityPlayerMP)e.getEntityPlayer());
  }
 }
 /** A wind burst has no ordinary explosion sound or terrain feedback. Velocity follows the native explosion packet. */
 public static final class Feedback implements IMessage {
  public UUID recipient;public int dimension,kind;public double x,y,z;public float radius,vx,vy,vz;
  public Feedback(){}
  public Feedback(EntityPlayer p,int kind,double x,double y,double z,float radius,double vx,double vy,double vz){recipient=p.getUniqueID();dimension=p.dimension;this.kind=kind;this.x=x;this.y=y;this.z=z;this.radius=radius;this.vx=(float)vx;this.vy=(float)vy;this.vz=(float)vz;}
  public void fromBytes(ByteBuf b){recipient=new UUID(b.readLong(),b.readLong());dimension=b.readInt();kind=b.readUnsignedByte();x=b.readDouble();y=b.readDouble();z=b.readDouble();radius=b.readFloat();vx=b.readFloat();vy=b.readFloat();vz=b.readFloat();if(kind>2||!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z)||!(radius>=0&&radius<=8)||!Float.isFinite(vx)||!Float.isFinite(vy)||!Float.isFinite(vz))throw new IllegalArgumentException("Invalid feedback event");}
  public void toBytes(ByteBuf b){b.writeLong(recipient.getMostSignificantBits());b.writeLong(recipient.getLeastSignificantBits());b.writeInt(dimension);b.writeByte(kind);b.writeDouble(x);b.writeDouble(y);b.writeDouble(z);b.writeFloat(radius);b.writeFloat(vx);b.writeFloat(vy);b.writeFloat(vz);}
 }
 public static final class FeedbackHandler implements IMessageHandler<Feedback,IMessage>{public IMessage onMessage(Feedback m,MessageContext ctx){ModernCombatMovement.proxy.feedback(m);return null;}}
 public static void effect(net.minecraft.world.World world,int kind,double x,double y,double z,float radius){if(world.isRemote)return;for(Object object:world.playerEntities){EntityPlayer p=(EntityPlayer)object;if(p instanceof EntityPlayerMP&&p.getDistanceSq(x,y,z)<4096)channel.sendTo(new Feedback(p,kind,x,y,z,radius,0,0,0),(EntityPlayerMP)p);}}
 public static final class CreativeBoost implements IMessage {
  public int dimension;public float multiplier;
  public CreativeBoost(){}public CreativeBoost(EntityPlayer p,float multiplier){dimension=p.dimension;this.multiplier=multiplier;}
  public void fromBytes(ByteBuf b){dimension=b.readInt();multiplier=b.readFloat();if(!(multiplier>=1&&multiplier<=5))throw new IllegalArgumentException("Invalid creative flight multiplier");}
  public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeFloat(multiplier);}
 }
 public static final class CreativeBoostHandler implements IMessageHandler<CreativeBoost,IMessage>{public IMessage onMessage(CreativeBoost m,MessageContext ctx){BOOSTS.put(ctx.getServerHandler().player.getUniqueID(),m);return null;}}
 public static final class Flight implements IMessage {public void fromBytes(ByteBuf b){}public void toBytes(ByteBuf b){}}
 public static final class FlightHandler implements IMessageHandler<Flight,IMessage>{public IMessage onMessage(Flight m,MessageContext ctx){FLIGHTS.put(ctx.getServerHandler().player.getUniqueID(),Boolean.TRUE);return null;}}
 public static final class Jab implements IMessage {public void fromBytes(ByteBuf b){}public void toBytes(ByteBuf b){}}
 public static final class JabHandler implements IMessageHandler<Jab,IMessage>{public IMessage onMessage(Jab m,MessageContext ctx){JABS.put(ctx.getServerHandler().player.getUniqueID(),Boolean.TRUE);return null;}}
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
  public int kineticHitTicks;public float flightLimit;public int dimension,id,policy; public UUID uuid; public Pose pose; public boolean swimming; public int spinTicks; public String registry;
  public Snapshot() {}
  public Snapshot(EntityPlayer p) { kineticHitTicks=PlayerHooks.state(p).kineticHitTicks;flightLimit=Settings.creativeFlightLimit;dimension=p.dimension; id=p.getEntityId(); uuid=p.getUniqueID(); policy=Settings.localPolicy; pose=PlayerHooks.state(p).pose; swimming=PlayerHooks.state(p).swimming; spinTicks=PlayerHooks.state(p).spinTicks;registry=RegistryAgreement.signature(); }
  public void fromBytes(ByteBuf b) { kineticHitTicks=b.readUnsignedByte();if(kineticHitTicks>10)throw new IllegalArgumentException("Invalid kinetic feedback");flightLimit=b.readFloat();if(!(flightLimit>=1&&flightLimit<=5))throw new IllegalArgumentException("Invalid flight policy");dimension=b.readInt(); id=b.readInt(); uuid=new UUID(b.readLong(),b.readLong()); policy=b.readUnsignedShort(); int index=b.readUnsignedByte(); if(index>=Pose.values().length || (policy&~1023)!=0) throw new IllegalArgumentException("Invalid MCM snapshot"); pose=Pose.values()[index]; swimming=b.readBoolean();registry=net.minecraftforge.fml.common.network.ByteBufUtils.readUTF8String(b);if(registry.length()>512)throw new IllegalArgumentException("Invalid MCM registry signature");spinTicks=b.readUnsignedByte();if(spinTicks>20)throw new IllegalArgumentException("Invalid Riptide timer"); }
  public void toBytes(ByteBuf b) { b.writeByte(kineticHitTicks);b.writeFloat(flightLimit);b.writeInt(dimension); b.writeInt(id); b.writeLong(uuid.getMostSignificantBits()); b.writeLong(uuid.getLeastSignificantBits()); b.writeShort(policy); b.writeByte(pose.ordinal()); b.writeBoolean(swimming);net.minecraftforge.fml.common.network.ByteBufUtils.writeUTF8String(b,registry);b.writeByte(spinTicks); }
 }
 public static final class SnapshotHandler implements IMessageHandler<Snapshot,IMessage> {
  public IMessage onMessage(Snapshot m,MessageContext ctx) { ModernCombatMovement.proxy.receive(m); return null; }
 }
}
