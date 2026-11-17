package com.glowingfederal.moderncombatmovement.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
 public static final int MOVEMENT=1, SWIMMING=2, CRAWLING=4, KNOCKBACK=8, COMBAT=16, KIT=32, FLIGHT=64, SHIELDS=128, PROJECTILES=256, OFFHAND=512;
 public static int localPolicy=7;
 public static Configuration configuration;
 public static float creativeFlightLimit=5;
 public static boolean rendering=true, debug=false, fallbackEquipment=true;
 private static volatile int remotePolicy=1023;
 private Settings() {}
 public static void load(File file) {
  Configuration c=new Configuration(file); c.load();configuration=c;ClientOptions.load(c);creativeFlightLimit=(float)c.get("Flight","maximumCreativeFlightMultiplier",5D,"Server limit for the optional additional creative sprint flight multiplier. 1 disables additional boosting. Restart the server after editing.",1D,5D).getDouble();if(!Float.isFinite(creativeFlightLimit))creativeFlightLimit=1;creativeFlightLimit=Math.max(1,Math.min(5,creativeFlightLimit));
  migrate(c,"General","modernKitItems","Equipment");
  migrate(c,"Compatibility","standaloneEquipment","Equipment");
  migrate(c,"Movement","elytraFlight","Flight");
  boolean enabled=c.getBoolean("enabled","General",true,"Enable the modern gameplay systems. Both sides must install the mod.");
  boolean movement=c.getBoolean("enabled","Movement",true,"Use verified modern movement and pose rules.");
  boolean swimming=c.getBoolean("enabled","Swimming",true,"Requires Movement. Modern sprint swimming and water travel.");
  boolean crawling=c.getBoolean("enabled","Crawling",true,"Requires Movement and Swimming. Collision-driven only; no prone key.");
  localPolicy=enabled && movement ? MOVEMENT | (swimming ? SWIMMING : 0) | (swimming && crawling ? CRAWLING : 0) : 0;
  if(enabled && c.getBoolean("enabled","Combat",true,"Modern cooldowns and attacks for explicit item profiles. Unregistered modded items retain their own attacks.")) localPolicy|=COMBAT;
  if(enabled && c.getBoolean("enabled","Knockback",true,"Modern deterministic resistance and grounded/airborne knockback.")) localPolicy|=KNOCKBACK;
  c.addCustomCategoryComment("Offhand","Native on 1.12.2; legacy targets retain their persistent one-slot equipment inventory.");
  rendering=c.getBoolean("poseAnimations","Rendering",true,"Interpolate camera and basic third-person low poses.");
  fallbackEquipment=c.getBoolean("standaloneEquipment","Equipment",true,"Register standalone netherite and copper armor/tools only without UAB. No acquisition materials or worldgen.");
  debug=c.getBoolean("diagnostics","Debugging",false,"Extra state diagnostics.");
  if(enabled && c.getBoolean("modernKitItems","Equipment",true,"Modern behavior for owned combat-kit items and explicitly selected UAB items.")) localPolicy|=KIT;
  if(enabled && movement && c.getBoolean("elytraFlight","Flight",true,"Modern elytra takeoff, flight physics and rocket boosting for explicit native/MCM/UAB equipment."))localPolicy|=FLIGHT;
  if(enabled && c.getBoolean("enabled","Shields",true,"Modern blocking delay, direction, durability and weapon disabling for native/MCM/UAB shields."))localPolicy|=SHIELDS;
  if(enabled && c.getBoolean("enabled","Projectiles",true,"Modern owned crossbows, tridents and wind charges. Requires Equipment.modernKitItems; does not disable flight rockets."))localPolicy|=PROJECTILES;
  if(enabled && c.getBoolean("interactions","Offhand",true,"Legacy hand interactions. Persistent equipment storage and container slots remain available when disabled."))localPolicy|=OFFHAND;
  if(c.hasChanged()) c.save();
 }
 private static void migrate(Configuration c,String previous,String key,String destination){
  String source=previous.toLowerCase(java.util.Locale.ROOT),target=destination.toLowerCase(java.util.Locale.ROOT);
  if(!c.hasKey(target,key)){if(c.hasKey(source,key))c.moveProperty(source,key,target);else if(c.hasKey(previous,key))c.moveProperty(previous,key,target);}
 }
 public static boolean movement(boolean client) { return ((client ? remotePolicy : localPolicy)&MOVEMENT)!=0; }
 public static boolean swimming(boolean client) { return ((client ? remotePolicy : localPolicy)&SWIMMING)!=0; }
 public static boolean crawling(boolean client) { return ((client ? remotePolicy : localPolicy)&CRAWLING)!=0; }
 public static boolean offhand(boolean client){return ((client?remotePolicy:localPolicy)&OFFHAND)!=0;}
 public static boolean projectiles(boolean client){return kit(client)&&((client?remotePolicy:localPolicy)&PROJECTILES)!=0;}
 public static boolean shields(boolean client){return ((client?remotePolicy:localPolicy)&SHIELDS)!=0;}
 public static boolean flight(boolean client){return ((client?remotePolicy:localPolicy)&FLIGHT)!=0;}
 public static boolean kit(boolean client) { return ((client ? remotePolicy : localPolicy)&KIT)!=0; }
 public static boolean combat(boolean client) { return ((client ? remotePolicy : localPolicy)&COMBAT)!=0; }
 public static boolean knockback(boolean client) { return ((client ? remotePolicy : localPolicy)&KNOCKBACK)!=0; }
 public static void acceptPolicy(int flags) { remotePolicy=flags&1023; }
 public static void resetRemote() { remotePolicy=1023;ClientOptions.serverFlightLimit=1; }
}
