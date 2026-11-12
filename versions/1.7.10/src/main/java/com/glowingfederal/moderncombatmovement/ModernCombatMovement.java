package com.glowingfederal.moderncombatmovement;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.compat.Content;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mod(modid=Identity.MOD_ID, name=Identity.NAME, version=BuildVersion.VERSION, acceptedMinecraftVersions="[1.7.10]", dependencies="required-after:modernregen;required-after:unimixins", acceptableRemoteVersions="${fullVersion}")
public final class ModernCombatMovement {
 @SidedProxy(clientSide="com.glowingfederal.moderncombatmovement.render.ClientProxy",serverSide="com.glowingfederal.moderncombatmovement.CommonProxy")
 public static CommonProxy proxy;
 @cpw.mods.fml.common.network.NetworkCheckHandler public boolean compatible(java.util.Map<String,String> mods, cpw.mods.fml.relauncher.Side side) { return BuildVersion.VERSION.equals(mods.get(Identity.MOD_ID)); }
 @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
  Settings.load(event.getSuggestedConfigurationFile());
  com.glowingfederal.moderncombatmovement.compat.VanillaProfiles.register();
  Packets.init(); Content.register(); proxy.init();
  event.getModLog().info(Identity.NAME + " " + BuildVersion.VERSION + ": protocol="+Identity.PROTOCOL+", movement policy="+Settings.localPolicy+", UAB="+Content.uab);
 }
 @Mod.EventHandler public void loaded(cpw.mods.fml.common.event.FMLLoadCompleteEvent event) { com.glowingfederal.moderncombatmovement.api.CombatApi.freeze(); }
}
