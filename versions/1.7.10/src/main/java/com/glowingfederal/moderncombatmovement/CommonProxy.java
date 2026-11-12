package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.network.Packets;
import net.minecraft.entity.player.EntityPlayer;
public class CommonProxy {
 public void init() {}
 public void receive(Packets.Snapshot packet) {}
 public boolean local(EntityPlayer player) { return false; }
 public void input(EntityPlayer player) {}
}
