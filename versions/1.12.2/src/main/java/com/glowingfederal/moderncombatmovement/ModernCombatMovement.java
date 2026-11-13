package com.glowingfederal.moderncombatmovement;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.compat.Content;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mod(modid=Identity.MOD_ID, name=Identity.NAME, version=BuildVersion.VERSION, acceptedMinecraftVersions="[1.12.2]", dependencies="after:etfuturum;required-after:mixinbooter", acceptableRemoteVersions=BuildVersion.VERSION)
public final class ModernCombatMovement implements zone.rong.mixinbooter.ILateMixinLoader {
 public java.util.List<String> getMixinConfigs() { return net.minecraftforge.fml.common.Loader.isModLoaded("etfuturum")?java.util.Collections.singletonList("mixins.moderncombatmovement.uab.json"):java.util.Collections.<String>emptyList(); }
 @Mod.Instance(Identity.MOD_ID) public static ModernCombatMovement instance;
 @SidedProxy(clientSide="com.glowingfederal.moderncombatmovement.render.ClientProxy",serverSide="com.glowingfederal.moderncombatmovement.CommonProxy")
 public static CommonProxy proxy;
 @net.minecraftforge.fml.common.network.NetworkCheckHandler public boolean compatible(java.util.Map<String,String> mods, net.minecraftforge.fml.relauncher.Side side) { return BuildVersion.VERSION.equals(mods.get(Identity.MOD_ID)); }
 @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
  Settings.load(event.getSuggestedConfigurationFile());
  com.glowingfederal.moderncombatmovement.compat.VanillaProfiles.register();
  Packets.init(); Content.register(); com.glowingfederal.moderncombatmovement.compat.Crossbows.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.Totems.register(); com.glowingfederal.moderncombatmovement.compat.Anchors.register(); com.glowingfederal.moderncombatmovement.compat.KitEffects.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.ModernEquipment.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.movement.FlightHooks.init(); proxy.init();
  event.getModLog().info(Identity.NAME + " " + BuildVersion.VERSION + ": protocol="+Identity.PROTOCOL+", movement policy="+Settings.localPolicy+", UAB="+Content.uab);
 }
 @Mod.EventHandler public void loaded(net.minecraftforge.fml.common.event.FMLLoadCompleteEvent event) { com.glowingfederal.moderncombatmovement.api.CombatApi.freeze(); }
}
