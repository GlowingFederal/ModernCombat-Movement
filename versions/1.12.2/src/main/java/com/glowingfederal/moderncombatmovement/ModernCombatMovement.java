package com.glowingfederal.moderncombatmovement;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.compat.Content;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mod(modid=Identity.MOD_ID, name=Identity.NAME, version=BuildVersion.VERSION, acceptedMinecraftVersions="[1.12.2]", dependencies="required-after:mixinbooter", acceptableRemoteVersions="${fullVersion}")
public final class ModernCombatMovement {
 @SidedProxy(clientSide="com.glowingfederal.moderncombatmovement.render.ClientProxy",serverSide="com.glowingfederal.moderncombatmovement.CommonProxy")
 public static CommonProxy proxy;
 @net.minecraftforge.fml.common.network.NetworkCheckHandler public boolean compatible(java.util.Map<String,String> mods, net.minecraftforge.fml.relauncher.Side side) { return BuildVersion.VERSION.equals(mods.get(Identity.MOD_ID)); }
 @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
  Settings.load(event.getSuggestedConfigurationFile());
  com.glowingfederal.moderncombatmovement.compat.VanillaProfiles.register();
  Packets.init(); Content.register(); proxy.init();
  event.getModLog().info(Identity.NAME + " " + BuildVersion.VERSION + ": protocol="+Identity.PROTOCOL+", movement policy="+Settings.localPolicy+", UAB="+Content.uab);
 }
 @Mod.EventHandler public void loaded(net.minecraftforge.fml.common.event.FMLLoadCompleteEvent event) { com.glowingfederal.moderncombatmovement.api.CombatApi.freeze(); }
}
