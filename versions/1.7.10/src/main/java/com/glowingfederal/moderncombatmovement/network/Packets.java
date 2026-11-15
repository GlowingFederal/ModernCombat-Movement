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
 private static final ConcurrentHashMap<UUID,OffhandUse> USES=new ConcurrentHashMap<UUID,OffhandUse>();
 private static final ConcurrentHashMap<UUID,OffhandAction> HANDS=new ConcurrentHashMap<UUID,OffhandAction>();
 public static SimpleNetworkWrapper channel;
 private Packets() {}
 public static void init() {
  channel=NetworkRegistry.INSTANCE.newSimpleChannel("mcm_state");
  channel.registerMessage(IntentHandler.class,Intent.class,0,Side.SERVER);
  channel.registerMessage(SnapshotHandler.class,Snapshot.class,1,Side.CLIENT);
  channel.registerMessage(JabHandler.class,Jab.class,2,Side.SERVER);
  channel.registerMessage(FlightHandler.class,Flight.class,3,Side.SERVER);
  channel.registerMessage(OffhandHandler.class,OffhandAction.class,4,Side.SERVER);
  channel.registerMessage(OffhandUseHandler.class,OffhandUse.class,5,Side.SERVER);
  cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new Events());
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static void consume(EntityPlayer p) {
  OffhandAction action=HANDS.remove(p.getUniqueID());if(action!=null&&action.dimension==p.dimension){if(action.slot<0)com.glowingfederal.moderncombatmovement.offhand.Offhands.swap(p);else com.glowingfederal.moderncombatmovement.offhand.Offhands.swapSlot(p,action.window,action.slot);}
  OffhandUse use=USES.remove(p.getUniqueID());if(use!=null&&p instanceof EntityPlayerMP)com.glowingfederal.moderncombatmovement.offhand.OffhandUses.handle((EntityPlayerMP)p,use);
  if(FLIGHTS.remove(p.getUniqueID())!=null&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.raw(p)&&!com.glowingfederal.moderncombatmovement.movement.FlightHooks.start(p))com.glowingfederal.moderncombatmovement.movement.FlightHooks.stop(p);
  if(JABS.remove(p.getUniqueID())!=null)com.glowingfederal.moderncombatmovement.combat.SpearMechanics.jab(p);
  Boolean jump=INPUT.remove(p.getUniqueID());
  if(jump!=null) PlayerHooks.state(p).jump=jump.booleanValue();
 }
 public static void publish(EntityPlayer p) {
  PlayerState s=PlayerHooks.state(p); int bits=s.pose.ordinal() | (s.swimming ? 4 : 0) | (Settings.localPolicy<<3);
  com.glowingfederal.moderncombatmovement.offhand.OffhandInventory hand=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p);
  boolean equipment=hand.swingSerial!=hand.lastSwingSerial||!net.minecraft.item.ItemStack.areItemStacksEqual(hand.lastSent,com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p))||hand.lastUsing!=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p);
  if(bits==s.lastSent && !equipment && s.shieldCooldown!=100 && s.windCooldown!=10 && p.ticksExisted%20!=0) return;
  hand.lastSwingSerial=hand.swingSerial;hand.lastSent=net.minecraft.item.ItemStack.copyItemStack(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p));hand.lastUsing=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p);
  s.lastSent=bits;
  Snapshot packet=new Snapshot(p);
  ((WorldServer)p.worldObj).getEntityTracker().func_151247_a(p,channel.getPacketFrom(packet));
  channel.sendTo(packet,(EntityPlayerMP)p);
 }
 public static final class Events {
  @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if(e.player instanceof EntityPlayerMP) channel.sendTo(new Snapshot(e.player),(EntityPlayerMP)e.player); }
  @SubscribeEvent public void logout(PlayerEvent.PlayerLoggedOutEvent e) { USES.remove(e.player.getUniqueID());HANDS.remove(e.player.getUniqueID());INPUT.remove(e.player.getUniqueID());JABS.remove(e.player.getUniqueID());FLIGHTS.remove(e.player.getUniqueID()); }
  @SubscribeEvent public void start(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking e) {
   if(e.target instanceof EntityPlayer && e.entityPlayer instanceof EntityPlayerMP) channel.sendTo(new Snapshot((EntityPlayer)e.target),(EntityPlayerMP)e.entityPlayer);
  }
 }
 public static final class OffhandUse implements IMessage {
  public int dimension,selected,x,y,z,side=-1,entity=-1;public float hx,hy,hz;public boolean release;
  public OffhandUse(){} public OffhandUse(EntityPlayer p,boolean release){dimension=p.dimension;selected=p.inventory.currentItem;this.release=release;}
  public void fromBytes(ByteBuf b){dimension=b.readInt();selected=b.readUnsignedByte();release=b.readBoolean();entity=b.readInt();side=b.readByte();x=b.readInt();y=b.readInt();z=b.readInt();hx=b.readFloat();hy=b.readFloat();hz=b.readFloat();if(entity< -1||entity>=0&&side>=0||selected>8||side< -1||side>5||!(hx>=0&&hx<=1&&hy>=0&&hy<=1&&hz>=0&&hz<=1))throw new IllegalArgumentException("Invalid offhand interaction");}
  public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeByte(selected);b.writeBoolean(release);b.writeInt(entity);b.writeByte(side);b.writeInt(x);b.writeInt(y);b.writeInt(z);b.writeFloat(hx);b.writeFloat(hy);b.writeFloat(hz);}
 }
 public static final class OffhandUseHandler implements IMessageHandler<OffhandUse,IMessage>{public IMessage onMessage(OffhandUse m,MessageContext ctx){USES.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
 public static final class OffhandAction implements IMessage {
  public int dimension,window,slot;
  public OffhandAction(){}public OffhandAction(EntityPlayer p,int slot){dimension=p.dimension;window=p.openContainer.windowId;this.slot=slot;}
  public void fromBytes(ByteBuf b){dimension=b.readInt();window=b.readInt();slot=b.readInt();if(slot< -1||slot>32767)throw new IllegalArgumentException("Invalid offhand slot");}
  public void toBytes(ByteBuf b){b.writeInt(dimension);b.writeInt(window);b.writeInt(slot);}
 }
 public static final class OffhandHandler implements IMessageHandler<OffhandAction,IMessage>{public IMessage onMessage(OffhandAction m,MessageContext ctx){HANDS.put(ctx.getServerHandler().playerEntity.getUniqueID(),m);return null;}}
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
  public int dimension,id,policy; public UUID uuid; public Pose pose; public boolean swimming; public net.minecraft.item.ItemStack offhand; public int offhandUse,shieldCooldown,windCooldown,offhandSwing,offhandSwingSerial;
  public Snapshot() {}
  public Snapshot(EntityPlayer p) { dimension=p.dimension; id=p.getEntityId(); uuid=p.getUniqueID(); policy=Settings.localPolicy; pose=PlayerHooks.state(p).pose; swimming=PlayerHooks.state(p).swimming; offhand=net.minecraft.item.ItemStack.copyItemStack(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p));offhandSwing=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p).swingTicks;offhandSwingSerial=com.glowingfederal.moderncombatmovement.offhand.Offhands.inventory(p).swingSerial;shieldCooldown=PlayerHooks.state(p).shieldCooldown;windCooldown=PlayerHooks.state(p).windCooldown;offhandUse=com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useRemaining():0; }
  public void fromBytes(ByteBuf b) { dimension=b.readInt(); id=b.readInt(); uuid=new UUID(b.readLong(),b.readLong()); policy=b.readUnsignedShort(); int index=b.readUnsignedByte(); if(index>=Pose.values().length || (policy&~1023)!=0) throw new IllegalArgumentException("Invalid MCM snapshot"); pose=Pose.values()[index]; swimming=b.readBoolean(); offhand=cpw.mods.fml.common.network.ByteBufUtils.readItemStack(b);offhandSwingSerial=b.readInt();offhandSwing=b.readUnsignedByte();if(offhandSwing>6)throw new IllegalArgumentException("Invalid offhand swing");windCooldown=b.readUnsignedByte();if(windCooldown>10)throw new IllegalArgumentException("Invalid wind cooldown");shieldCooldown=b.readUnsignedByte();if(shieldCooldown>100)throw new IllegalArgumentException("Invalid shield cooldown");offhandUse=b.readInt();if(offhandUse<0||offhandUse>72000)throw new IllegalArgumentException("Invalid offhand use"); }
  public void toBytes(ByteBuf b) { b.writeInt(dimension); b.writeInt(id); b.writeLong(uuid.getMostSignificantBits()); b.writeLong(uuid.getLeastSignificantBits()); b.writeShort(policy); b.writeByte(pose.ordinal()); b.writeBoolean(swimming); cpw.mods.fml.common.network.ByteBufUtils.writeItemStack(b,offhand);b.writeInt(offhandSwingSerial);b.writeByte(offhandSwing);b.writeByte(windCooldown);b.writeByte(shieldCooldown);b.writeInt(offhandUse); }
 }
 public static final class SnapshotHandler implements IMessageHandler<Snapshot,IMessage> {
  public IMessage onMessage(Snapshot m,MessageContext ctx) { ModernCombatMovement.proxy.receive(m); return null; }
 }
}
