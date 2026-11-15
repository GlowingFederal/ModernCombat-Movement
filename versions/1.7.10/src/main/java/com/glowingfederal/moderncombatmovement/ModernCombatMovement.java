package com.glowingfederal.moderncombatmovement;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.compat.Content;
import com.glowingfederal.moderncombatmovement.network.Packets;
@Mod(modid=Identity.MOD_ID, name=Identity.NAME, version=BuildVersion.VERSION, acceptedMinecraftVersions="[1.7.10]", dependencies="after:etfuturum;after:lotr;required-after:modernregen@[1.0.2.2,);required-after:unimixins", acceptableRemoteVersions=BuildVersion.VERSION)
public final class ModernCombatMovement {
 @Mod.Instance(Identity.MOD_ID) public static ModernCombatMovement instance;
 @SidedProxy(clientSide="com.glowingfederal.moderncombatmovement.render.ClientProxy",serverSide="com.glowingfederal.moderncombatmovement.CommonProxy")
 public static CommonProxy proxy;
 @cpw.mods.fml.common.network.NetworkCheckHandler public boolean compatible(java.util.Map<String,String> mods, cpw.mods.fml.relauncher.Side side) { return BuildVersion.VERSION.equals(mods.get(Identity.MOD_ID)); }
 @Mod.EventHandler public void preInit(FMLPreInitializationEvent event) {
  Settings.load(event.getSuggestedConfigurationFile());
  com.glowingfederal.moderncombatmovement.compat.VanillaProfiles.register();
  Packets.init(); Content.register(); com.glowingfederal.moderncombatmovement.compat.Shields.register(); com.glowingfederal.moderncombatmovement.compat.Crossbows.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.Totems.register(); com.glowingfederal.moderncombatmovement.compat.Anchors.register(); com.glowingfederal.moderncombatmovement.compat.KitEffects.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.ModernEquipment.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.Tridents.register(event.getSuggestedConfigurationFile()); com.glowingfederal.moderncombatmovement.compat.WindCharges.register(); com.glowingfederal.moderncombatmovement.movement.FlightHooks.init(); proxy.init();
  event.getModLog().info(Identity.NAME + " " + BuildVersion.VERSION + ": protocol="+Identity.PROTOCOL+", movement policy="+Settings.localPolicy+", UAB="+Content.uab);
 }
 @Mod.EventHandler public void loaded(cpw.mods.fml.common.event.FMLLoadCompleteEvent event) { com.glowingfederal.moderncombatmovement.combat.CombatConsumables.removeLegacyAppleRecipe(); com.glowingfederal.moderncombatmovement.api.CombatApi.freeze(); }
}
