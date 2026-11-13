package com.glowingfederal.moderncombatmovement.combat;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
/** Marks our explosions so armor and hurt knockback have exactly one owner. */
public final class CombatBlastDamageSource extends DamageSource {
 private final Entity direct,owner;
 private final double x,y,z;
 public CombatBlastDamageSource(Entity direct,Entity owner,double x,double y,double z){super(owner==null?"explosion":"explosion.player");this.direct=direct;this.owner=owner;this.x=x;this.y=y;this.z=z;setExplosion();setDifficultyScaled();}
 @Override public Entity getSourceOfDamage(){return direct;}
 @Override public Entity getEntity(){return owner;}
}
