package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.world.World;
/** Read the native server spawn permission without introducing a second spawn policy. */
public final class WorldSpawnRules {
 private WorldSpawnRules(){}
 private static final java.lang.reflect.Field HOSTILE=field();
 private static java.lang.reflect.Field field(){
  for(String name:new String[]{"spawnHostileMobs","field_72985_G"})try{java.lang.reflect.Field f=World.class.getDeclaredField(name);f.setAccessible(true);return f;}catch(NoSuchFieldException absent){}
  throw new IllegalStateException("Native world hostile-spawn permission is unavailable");
 }
 public static boolean hostile(World world){try{return HOSTILE.getBoolean(world);}catch(IllegalAccessException impossible){throw new IllegalStateException(impossible);}}
}
