package com.glowingfederal.moderncombatmovement.movement;
/** Vanilla fly_into_wall damage bypasses armor, while ordinary protection still applies. */
public final class FlightWallDamage extends net.minecraft.util.DamageSource {public FlightWallDamage(){super("flyIntoWall");setDamageBypassesArmor();}}
