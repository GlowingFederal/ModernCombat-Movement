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
 private static final ConcurrentHashMap<UUID,OffhandUse> USES=new ConcurrentHashMap<UUID,OffhandUse>();
 private static final ConcurrentHashMap<UUID,OffhandAction> HANDS=new ConcurrentHashMap<UUID,OffhandAction>();
 private static final ConcurrentHashMap<UUID,CreativeBoost> BOOSTS=new ConcurrentHashMap<UUID,CreativeBoost>();
 private static final ConcurrentHashMap<UUID,Miss> MISSES=new ConcurrentHashMap<UUID,Miss>();
 public static SimpleNetworkWrapper channel;
 private Packets() {}
 public static void init() {
  channel=NetworkRegistry.INSTANCE.newSimpleChannel("mcm_state");
  channel.registerMessage(MissHandler.class,Miss.class,8,Side.SERVER);
  channel.registerMessage(FeedbackHandler.class,Feedback.class,7,Side.CLIENT);
  channel.registerMessage(CreativeBoostHandler.class,CreativeBoost.class,6,Side.SERVER);
  channel.registerMessage(IntentHandler.class,Intent.class,0,Side.SERVER);
  channel.registerMessage(SnapshotHandler.class,Snapshot.class,1,Side.CLIENT);
  channel.registerMessage(JabHandler.class,Jab.class,2,Side.SERVER);
  channel.registerMessage(FlightHandler.class,Flight.class,3,Side.SERVER);
  channel.registerMessage(OffhandHandler.class,OffhandAction.class,4,Side.SERVER);
  channel.registerMessage(OffhandUseHandler.class,OffhandUse.class,5,Side.SERVER);
  net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(new Events());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void consume(EntityPlayer p) {
  Miss miss=MISSES.remove(p.getUniqueID());if(miss!=null&&miss.id==p.getEntityId()&&miss.dimension==p.dimension&&miss.selected==p.inventory.currentItem&&p.isEntityAlive()&&Settings.combat(false)&&com.glowingfederal.moderncombatmovement.combat.CombatHooks.profile(p)!=null&&!com.glowingfederal.moderncombatmovement.combat.SpearMechanics.owned(p))PlayerHooks.state(p).attackTicks=0;
  CreativeBoost boost=BOOSTS.remove(p.getUniqueID());if(boost!=null&&boost.dimension==p.dimension){PlayerState state=PlayerHooks.state(p);if(boost.multiplier==1)state.flightBoostBlocked=false;state.requestedFlightBoost=Math.min(boost.multiplier,Settings.creativeFlightLimit);state.flightBoostLease=40;}
  OffhandAction action=HANDS.remove(p.getUniqueID());if(action!=null&&action.dimension==p.dimension){if(action.slot<0)com.glowingfederal.moderncombatmovement.offhand.Offhands.swap(p);else com.glowingfederal.moderncombatmovement.offhand.Offhands.swapSlot(p,action.window,action.slot);}
  OffhandUse use=USES.remove(p.getUniqueID());if(use!=null&&p instanceof EntityPlayerMP)com.glowingfederal.moderncombatmovement.offhand.OffhandUses.handle((EntityPlayerMP)p,use);
  if(FLIGHTS.remove(p.getUniqueID())!=null&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.raw(p)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))com.glowingfederal.moderncombatmovement.movement.FlightHooks.stop(p);
  if(JABS.remove(p.getUniqueID())!=null)com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);
  Boolean jump=INPUT.remove(p.getUniqueID());
  if(jump!=null) PlayerHooks.state(p).jump=jump.booleanValue();
 }
 public static void publish(EntityPlayer p) {
  if(!(p instanceof EntityPlayerMP))return;
  EntityPlayerMP recipient=(EntityPlayerMP)p;
  if(recipient.playerNetServerHandler==null||recipient.playerNetServerHandler.netManager==null||!recipient.playerNetServerHandler.netManager.isChannelOpen())return;
  PlayerState s=PlayerHooks.state(p); int bits=s.pose.ordinal() | (s.swimming ? 8 : 0) | (Settings.localPolicy<<4);
  com.glowingfederal.moderncombatmovement.offhand.OffhandInventory hand=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p);
  boolean equipment=hand.swingSerial!=hand.lastSwingSerial||!net.minecraft.item.ItemStack.areItemStacksEqual(hand.lastSent,com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p))||hand.lastUsing!=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p);
  boolean cooldown=s.shieldCooldown>s.lastShieldCooldown||s.windCooldown>s.lastWindCooldown||s.pearlCooldown>s.lastPearlCooldown;
  s.lastShieldCooldown=s.shieldCooldown;s.lastWindCooldown=s.windCooldown;s.lastPearlCooldown=s.pearlCooldown;
  if(bits==s.lastSent && !equipment && !cooldown && p.ticksExisted%20!=0) return;
  hand.lastSwingSerial=hand.swingSerial;hand.lastSent=net.minecraft.item.ItemStack.copyItemStack(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p));hand.lastUsing=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p);
  s.lastSent=bits;
  Snapshot packet=new Snapshot(p);
  ((WorldServer)p.worldObj).getEntityTracker().sendToAllTrackingEntity(p,channel.getPacketFrom(packet));
  channel.sendTo(packet,(EntityPlayerMP)p);
 }
 public static final class Events {
  @SubscribeEvent public void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone e) { com.glowingfederal.moderncombatmovement.movement.CreativeFlight.clone(e.original,e.entityPlayer); }
  @SubscribeEvent public void dimension(PlayerEvent.PlayerChangedDimensionEvent e) { MISSES.remove(e.player.getUniqueID());BOOSTS.remove(e.player.getUniqueID());com.glowingfederal.moderncombatmovement.movement.CreativeFlight.restore(e.player,true); }
  @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if(e.player instanceof EntityPlayerMP) channel.sendTo(new Snapshot(e.player),(EntityPlayerMP)e.player); }
  @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e) { MISSES.remove(e.player.getUniqueID());BOOSTS.remove(e.player.getUniqueID());com.glowingfederal.moderncombatmovement.movement.CreativeFlight.restore(e.player,false); USES.remove(e.player.getUniqueID());HANDS.remove(e.player.getUniqueID());INPUT.remove(e.player.getUniqueID());JABS.remove(e.player.getUniqueID());FLIGHTS.remove(e.player.getUniqueID()); }
  @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e) {
   if(e.target instanceof EntityPlayer && e.entityPlayer instanceof EntityPlayerMP) channel.sendTo(new Snapshot((EntityPlayer)e.target),(EntityPlayerMP)e.entityPlayer);
  }
 }
 public static final class OffhandUse implements IMessage {
  public int playerId,dimension,selected,x,y,z,side=-1,entity=-1;public float hx,hy,hz;public boolean release;public int serial;
  public OffhandUse(){} public OffhandUse(EntityPlayer p,boolean release){playerId=p.getEntityId();dimension=p.dimension;selected=p.inventory.currentItem;this.release=release;serial=com.glowingfederal.moderncombatmovement.pose.UseSequence.next(PlayerHooks.state(p).offhandIntentSerial);PlayerHooks.state(p).offhandIntentSerial=serial;}
  public void fromBytes(ByteBuf b){playerId=b.readInt();dimension=b.readInt();selected=b.readUnsignedByte();release=b.readBoolean();serial=b.readInt();entity=b.readInt();side=b.readByte();x=b.readInt();y=b.readInt();z=b.readInt();hx=b.readFloat();hy=b.readFloat();hz=b.readFloat();if(entity< -1||entity>=0&&side>=0||selected>8||side< -1||side>5||!(hx>=0&&hx<=1&&hy>=0&&hy<=1&&hz>=0&&hz<=1))throw new IllegalArgumentException("Invalid offhand interaction");}
  public void toBytes(ByteBuf b){b.writeInt(playerId);b.writeInt(dimension);b.writeByte(selected);b.writeBoolean(release);b.writeInt(serial);b.writeInt(entity);b.writeByte(side);b.writeInt(x);b.writeInt(y);b.writeInt(z);b.writeFloat(hx);b.writeFloat(hy);b.writeFloat(hz);}
 }
 public static final class OffhandUseHandler implements IMessageHandler<OffhandUse,IMessage>{public IMessage onMessage(OffhandUse m,MessageContext ctx){USES.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
 public static final class OffhandAction implements IMessage {
  public int dimension,window,slot;
  public OffhandAction(){}public OffhandAction(EntityPlayer p,int slot){dimension=p.dimension;window=p.openContainer.windowId;this.slot=slot;}
  public void fromBytes(ByteBuf b){dimension=b.readInt();window=b.readInt();slot=b.readInt();if(slot< -1||slot>32767)throw new IllegalArgumentException("Invalid offhand slot");}
  public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(window);b.writeInt(slot);}
 }
 public static final class OffhandHandler implements IMessageHandler<OffhandAction,IMessage>{public IMessage onMessage(OffhandAction m,MessageContext ctx){HANDS.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
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
 public static final class CreativeBoostHandler implements IMessageHandler<CreativeBoost,IMessage>{public IMessage onMessage(CreativeBoost m,MessageContext ctx){BOOSTS.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
 public static final class Miss implements IMessage {
  public int id,dimension,selected;public Miss(){}public Miss(EntityPlayer p){id=p.getEntityId();dimension=p.dimension;selected=p.inventory.currentItem;}
  public void fromBytes(ByteBuf b){id=b.readInt();dimension=b.readInt();selected=b.readUnsignedByte();if(selected>8)throw new IllegalArgumentException("Invalid attack slot");}public void toBytes(ByteBuf b){b.writeInt(id);b.writeInt(dimension);b.writeByte(selected);}
 }
 public static final class MissHandler implements IMessageHandler<Miss,IMessage>{public IMessage onMessage(Miss m,MessageContext ctx){MISSES.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
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
  public int kineticHitTicks;public float flightLimit;public int dimension,id,policy;public int offhandAckSerial; public UUID uuid; public Pose pose; public boolean swimming; public int spinTicks; public String registry; public net.minecraft.item.ItemStack offhand; public int offhandUse,shieldCooldown,windCooldown,pearlCooldown,offhandSwing,offhandSwingSerial;
  public Snapshot() {}
  public Snapshot(EntityPlayer p) { kineticHitTicks=PlayerHooks.state(p).kineticHitTicks;flightLimit=Settings.creativeFlightLimit;dimension=p.dimension; id=p.getEntityId(); uuid=p.getUniqueID(); policy=Settings.localPolicy; pose=PlayerHooks.state(p).pose; swimming=PlayerHooks.state(p).swimming; spinTicks=PlayerHooks.state(p).spinTicks;registry=RegistryAgreement.signature(); offhand=net.minecraft.item.ItemStack.copyItemStack(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p));offhandSwing=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p).swingTicks;offhandSwingSerial=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p).swingSerial;offhandAckSerial=PlayerHooks.state(p).offhandAckSerial;shieldCooldown=PlayerHooks.state(p).shieldCooldown;windCooldown=PlayerHooks.state(p).windCooldown;pearlCooldown=PlayerHooks.state(p).pearlCooldown;offhandUse=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useRemaining():0; }
  public void fromBytes(ByteBuf b) { kineticHitTicks=b.readUnsignedByte();if(kineticHitTicks>10)throw new IllegalArgumentException("Invalid kinetic feedback");flightLimit=b.readFloat();if(!(flightLimit>=1&&flightLimit<=5))throw new IllegalArgumentException("Invalid flight policy");dimension=b.readInt(); id=b.readInt(); uuid=new UUID(b.readLong(),b.readLong()); policy=b.readUnsignedShort(); int index=b.readUnsignedByte(); if(index>=Pose.values().length || (policy&~1023)!=0) throw new IllegalArgumentException("Invalid MCM snapshot"); pose=Pose.values()[index]; swimming=b.readBoolean();registry=net.minecraftforge.fml.common.network.ByteBufUtils.readUTF8String(b);if(registry.length()>512)throw new IllegalArgumentException("Invalid MCM registry signature");spinTicks=b.readUnsignedByte();if(spinTicks>20)throw new IllegalArgumentException("Invalid Riptide timer"); offhand=net.minecraftforge.fml.common.network.ByteBufUtils.readItemStack(b);offhandSwingSerial=b.readInt();offhandAckSerial=b.readInt();offhandSwing=b.readUnsignedByte();if(offhandSwing>6)throw new IllegalArgumentException("Invalid offhand swing");pearlCooldown=b.readUnsignedByte();if(pearlCooldown>20)throw new IllegalArgumentException("Invalid pearl cooldown");windCooldown=b.readUnsignedByte();if(windCooldown>10)throw new IllegalArgumentException("Invalid wind cooldown");shieldCooldown=b.readUnsignedByte();if(shieldCooldown>100)throw new IllegalArgumentException("Invalid shield cooldown");offhandUse=b.readInt();if(offhandUse<0||offhandUse>72000)throw new IllegalArgumentException("Invalid offhand use"); }
  public void toBytes(ByteBuf b) { b.writeByte(kineticHitTicks);b.writeFloat(flightLimit);b.writeInt(dimension); b.writeInt(id); b.writeLong(uuid.getMostSignificantBits()); b.writeLong(uuid.getLeastSignificantBits()); b.writeShort(policy); b.writeByte(pose.ordinal()); b.writeBoolean(swimming);net.minecraftforge.fml.common.network.ByteBufUtils.writeUTF8String(b,registry);b.writeByte(spinTicks); net.minecraftforge.fml.common.network.ByteBufUtils.writeItemStack(b,offhand);b.writeInt(offhandSwingSerial);b.writeInt(offhandAckSerial);b.writeByte(offhandSwing);b.writeByte(pearlCooldown);b.writeByte(windCooldown);b.writeByte(shieldCooldown);b.writeInt(offhandUse); }
 }
 public static final class SnapshotHandler implements IMessageHandler<Snapshot,IMessage> {
  public IMessage onMessage(Snapshot m,MessageContext ctx) { ModernCombatMovement.proxy.receive(m); return null; }
 }
}
