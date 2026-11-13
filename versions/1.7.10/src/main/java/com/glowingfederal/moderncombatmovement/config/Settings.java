package com.glowingfederal.moderncombatmovement.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
 public static final int MOVEMENT=1, SWIMMING=2, CRAWLING=4, KNOCKBACK=8, COMBAT=16, KIT=32, FLIGHT=64;
 public static int localPolicy=7;
 public static boolean rendering=true, debug=false, fallbackEquipment=true;
 private static volatile int remotePolicy=127;
 private Settings() {}
 public static void load(File file) {
  Configuration c=new Configuration(file); c.load();
  boolean enabled=c.getBoolean("enabled","General",true,"Enable the modern gameplay systems. Both sides must install the mod.");
  boolean movement=c.getBoolean("enabled","Movement",true,"Use verified modern movement and pose rules.");
  boolean swimming=c.getBoolean("enabled","Swimming",true,"Requires Movement. Modern sprint swimming and water travel.");
  boolean crawling=c.getBoolean("enabled","Crawling",true,"Requires Movement and Swimming. Collision-driven only; no prone key.");
  localPolicy=enabled && movement ? MOVEMENT | (swimming ? SWIMMING : 0) | (swimming && crawling ? CRAWLING : 0) : 0;
  if(enabled && c.getBoolean("enabled","Combat",true,"Modern cooldowns and attacks for explicit item profiles. Unregistered modded items retain their own attacks.")) localPolicy|=COMBAT;
  if(enabled && c.getBoolean("enabled","Knockback",true,"Modern deterministic resistance and grounded/airborne knockback.")) localPolicy|=KNOCKBACK;
  c.addCustomCategoryComment("Offhand","Native on 1.12.2; legacy inventory migration pending.");
  rendering=c.getBoolean("poseAnimations","Rendering",true,"Interpolate camera and basic third-person low poses.");
  fallbackEquipment=c.getBoolean("standaloneEquipment","Compatibility",true,"Register standalone netherite and copper armor/tools only without UAB. No acquisition materials or worldgen.");
  debug=c.getBoolean("diagnostics","Debugging",false,"Extra state diagnostics.");
  boolean lotrMode=c.getBoolean("lotrCombatCompatibility","Compatibility",true,"When LOTR Legacy is loaded, retain its attack timing, reach and knockback pipeline. Movement remains independently configurable.");
  if(lotrMode && cpw.mods.fml.common.Loader.isModLoaded("lotr")) localPolicy&=~(COMBAT|KNOCKBACK);
  if(enabled && c.getBoolean("modernKitItems","General",true,"Modern behavior for owned combat-kit items and explicitly selected UAB items.")) localPolicy|=KIT;
  if(enabled && movement && c.getBoolean("elytraFlight","Movement",true,"Modern elytra takeoff, flight physics and rocket boosting for explicit native/MCM/UAB equipment."))localPolicy|=FLIGHT;
  if(c.hasChanged()) c.save();
 }
 public static boolean movement(boolean client) { return ((client ? remotePolicy : localPolicy)&MOVEMENT)!=0; }
 public static boolean swimming(boolean client) { return ((client ? remotePolicy : localPolicy)&SWIMMING)!=0; }
 public static boolean crawling(boolean client) { return ((client ? remotePolicy : localPolicy)&CRAWLING)!=0; }
 public static boolean flight(boolean client){return ((client?remotePolicy:localPolicy)&FLIGHT)!=0;}
 public static boolean kit(boolean client) { return ((client ? remotePolicy : localPolicy)&KIT)!=0; }
 public static boolean combat(boolean client) { return ((client ? remotePolicy : localPolicy)&COMBAT)!=0; }
 public static boolean knockback(boolean client) { return ((client ? remotePolicy : localPolicy)&KNOCKBACK)!=0; }
 public static void acceptPolicy(int flags) { remotePolicy=flags&127; }
 public static void resetRemote() { remotePolicy=127; }
}
