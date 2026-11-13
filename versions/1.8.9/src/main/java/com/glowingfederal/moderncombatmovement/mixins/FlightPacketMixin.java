package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
@Mixin(value=NetHandlerPlayServer.class,remap=false)
public abstract class FlightPacketMixin {
 @ModifyConstant(method={"processPlayer","func_147347_a"},constant=@Constant(doubleValue=100),require=0)private double mcm$speedLimit(double value){EntityPlayer p=((NetHandlerPlayServer)(Object)this).playerEntity;if(!FlightHooks.managed(p)||!FlightHooks.flying(p)||FlightHooks.uabFlight)return value;return p.worldObj.getGameRules().getBoolean("disableElytraMovementCheck")?Double.MAX_VALUE:300;}
 @Redirect(method={"processPlayer","func_147347_a"},at=@At(value="INVOKE",target="Lnet/minecraft/server/MinecraftServer;isFlightAllowed()Z"),require=0)private boolean mcm$legitimateFlightMcp(MinecraftServer server){EntityPlayer p=((NetHandlerPlayServer)(Object)this).playerEntity;return server.isFlightAllowed()||FlightHooks.managed(p)&&FlightHooks.flying(p);}
 @Redirect(method={"processPlayer","func_147347_a"},at=@At(value="INVOKE",target="Lnet/minecraft/server/MinecraftServer;func_71231_X()Z"),require=0)private boolean mcm$legitimateFlightSrg(MinecraftServer server){EntityPlayer p=((NetHandlerPlayServer)(Object)this).playerEntity;return server.isFlightAllowed()||FlightHooks.managed(p)&&FlightHooks.flying(p);}
}
