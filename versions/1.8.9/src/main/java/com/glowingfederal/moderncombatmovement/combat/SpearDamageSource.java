package com.glowingfederal.moderncombatmovement.combat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
/** Spear damage is player damage with an explicit knockback owner, matching modern no_knockback tags. */
public final class SpearDamageSource extends EntityDamageSource {
 public SpearDamageSource(EntityPlayer attacker){super("player",attacker);}
}
