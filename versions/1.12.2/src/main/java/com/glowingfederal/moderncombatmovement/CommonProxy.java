package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.network.Packets;
import net.minecraft.entity.player.EntityPlayer;
public class CommonProxy {
 public void init() {}
 public void fireworks(net.minecraft.world.World world,double x,double y,double z,double vx,double vy,double vz,net.minecraft.nbt.NBTTagCompound data){}
 public void receive(Packets.Snapshot packet) {}
 public void feedback(Packets.Feedback packet) {}
 public void windTrail(net.minecraft.world.World world,double x,double y,double z) {}
 public boolean local(EntityPlayer player) { return false; }
 public void input(EntityPlayer player) {}
}
