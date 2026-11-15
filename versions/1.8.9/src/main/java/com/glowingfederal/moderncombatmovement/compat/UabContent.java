package com.glowingfederal.moderncombatmovement.compat;
/** Optional content policy boundary. Disabled UAB content never acquires a duplicate standalone identity. */
final class UabContent {
 private UabContent(){}
 static boolean enabled(String key){try{return Boolean.TRUE.equals(Class.forName("ganymedes01.etfuturum.configuration.ContentAvailability").getMethod("enabled",String.class).invoke(null,key));}catch(ReflectiveOperationException missing){throw new IllegalStateException("Unsupported UAB content availability API",missing);}}
}
