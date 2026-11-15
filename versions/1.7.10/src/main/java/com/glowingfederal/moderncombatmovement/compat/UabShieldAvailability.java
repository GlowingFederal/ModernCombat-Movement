package com.glowingfederal.moderncombatmovement.compat;
/** Optional reflection boundary; UAB's disabled acquisition also disables adapted use. */
final class UabShieldAvailability {
 private static java.lang.reflect.Method enabled;
 private UabShieldAvailability(){}
 static boolean enabled(){
  try{if(enabled==null)enabled=Class.forName("ganymedes01.etfuturum.configuration.ContentAvailability").getMethod("enabled",String.class);return Boolean.TRUE.equals(enabled.invoke(null,"shields"));}
  catch(ReflectiveOperationException missing){throw new IllegalStateException("Unsupported UAB shield availability API",missing);}
 }
}
