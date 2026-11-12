package com.glowingfederal.moderncombatmovement.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
public final class Settings {
 public static final int MOVEMENT=1, SWIMMING=2, CRAWLING=4, KNOCKBACK=8, COMBAT=16;
 public static int localPolicy=7;
 public static boolean rendering=true, debug=false, fallbackEquipment=true;
 private static volatile int remotePolicy=31;
 private Settings() {}
 public static void load(File file) {
  Configuration c=new Configuration(file); c.load();
  boolean enabled=c.getBoolean("enabled","General",true,"Enable the movement milestone. Both sides must install the mod.");
  boolean movement=c.getBoolean("enabled","Movement",true,"Use verified modern movement and pose rules.");
  boolean swimming=c.getBoolean("enabled","Swimming",true,"Requires Movement. Modern sprint swimming and water travel.");
  boolean crawling=c.getBoolean("enabled","Crawling",true,"Requires Movement and Swimming. Collision-driven only; no prone key.");
  localPolicy=enabled && movement ? MOVEMENT | (swimming ? SWIMMING : 0) | (swimming && crawling ? CRAWLING : 0) : 0;
  if(enabled && c.getBoolean("enabled","Combat",true,"Modern cooldowns and attacks for explicit item profiles. Unregistered modded items retain their own attacks.")) localPolicy|=COMBAT;
  if(enabled && c.getBoolean("enabled","Knockback",true,"Modern deterministic resistance and grounded/airborne knockback; legacy extra sprint-hit migration remains pending.")) localPolicy|=KNOCKBACK;
  c.addCustomCategoryComment("Offhand","Native on 1.12.2; legacy inventory migration pending.");
  rendering=c.getBoolean("poseAnimations","Rendering",true,"Interpolate camera and basic third-person low poses.");
  fallbackEquipment=c.getBoolean("standaloneEquipment","Compatibility",true,"Register fallback netherite equipment only without UAB. No acquisition materials or worldgen.");
  debug=c.getBoolean("diagnostics","Debugging",false,"Extra state diagnostics.");
  boolean lotrMode=c.getBoolean("lotrCombatCompatibility","Compatibility",true,"When LOTR Legacy is loaded, retain its attack timing, reach and knockback pipeline. Movement remains independently configurable.");
  if(lotrMode && cpw.mods.fml.common.Loader.isModLoaded("lotr")) localPolicy&=~(COMBAT|KNOCKBACK);
  if(c.hasChanged()) c.save();
 }
 public static boolean movement(boolean client) { return ((client ? remotePolicy : localPolicy)&MOVEMENT)!=0; }
 public static boolean swimming(boolean client) { return ((client ? remotePolicy : localPolicy)&SWIMMING)!=0; }
 public static boolean crawling(boolean client) { return ((client ? remotePolicy : localPolicy)&CRAWLING)!=0; }
 public static boolean combat(boolean client) { return ((client ? remotePolicy : localPolicy)&COMBAT)!=0; }
 public static boolean knockback(boolean client) { return ((client ? remotePolicy : localPolicy)&KNOCKBACK)!=0; }
 public static void acceptPolicy(int flags) { remotePolicy=flags&31; }
 public static void resetRemote() { remotePolicy=31; }
}
