package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.Entity;
/** Explicit ownership confines modern armor and knockback adaptation to our attached rockets. */
public final class BoostRocketDamage extends net.minecraft.util.EntityDamageSourceIndirect {public BoostRocketDamage(Entity rocket,Entity owner){super("fireworks",rocket,owner);setExplosion();}}
