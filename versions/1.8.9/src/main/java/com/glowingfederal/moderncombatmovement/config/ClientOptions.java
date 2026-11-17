package com.glowingfederal.moderncombatmovement.config;
import net.minecraftforge.common.config.Configuration;
/** Client preferences do not change the server's gameplay policy. */
public final class ClientOptions {
 public static boolean toggleSprint,toggleSneak,flightBoost,cooldownOverlays=true;
 public static double flightMultiplier=2;
 public static String attackIndicator="CROSSHAIR";
 public static volatile float serverFlightLimit=1;
 private ClientOptions(){}
 public static void load(Configuration config){
  config.addCustomCategoryComment("Controls","Hold uses the normal key. Toggle changes state on each press. GUI, focus and lifecycle changes clear toggles. Better Sprinting takes priority when installed.");
  toggleSprint=config.getBoolean("toggleSprint","Controls",false,"Use the sprint key as a toggle. Off retains hold sprint and normal double tapping.");
  toggleSneak=config.getBoolean("toggleSneak","Controls",false,"Use the sneak key as a toggle. Off retains hold sneak.");
  flightBoost=config.getBoolean("creativeFlightBoost","Controls",false,"Apply an additional speed multiplier to creative sprint flight. Requires the server's permission; disabled when Better Sprinting handles controls.");
  flightMultiplier=config.get("Controls","creativeFlightMultiplier",2D,"Additional creative sprint flight multiplier. The server can impose a lower limit.",1D,5D).getDouble();
  if(!Double.isFinite(flightMultiplier))flightMultiplier=2;flightMultiplier=Math.max(1,Math.min(5,flightMultiplier));
  cooldownOverlays=config.getBoolean("itemCooldownOverlays","HUD",true,"Show the actual supported item reuse cooldown over inventory and hotbar icons.");
  attackIndicator=config.getString("attackIndicator","HUD","CROSSHAIR","Attack cooldown position. VANILLA retains the native Video Settings option where available.",new String[]{"CROSSHAIR","HOTBAR","OFF"});
 }
}
