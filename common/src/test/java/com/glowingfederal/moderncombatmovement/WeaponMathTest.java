package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.combat.*;
import org.junit.Test;
import static org.junit.Assert.*;

/** Reference cases from vanilla MaceItem, CombatRules, and ExplosionDamageCalculator. */
public class WeaponMathTest {
    @Test public void smashThresholdAndPiecewiseBoundaries() {
        assertEquals(0,WeaponMath.smashBonus(1.5,5),0);
        assertEquals(12,WeaponMath.smashBonus(3,0),0);
        assertEquals(22,WeaponMath.smashBonus(8,0),0);
        assertEquals(24,WeaponMath.smashBonus(10,0),0);
        assertEquals(49,WeaponMath.smashBonus(10,5),0);
    }
    @Test public void heavySmashAndResistance() {
        assertEquals(1.75,WeaponMath.smashKnockback(1,5,0),1e-6);
        assertEquals(3.5,WeaponMath.smashKnockback(1,5.01,0),1e-6);
        assertEquals(0,WeaponMath.smashKnockback(1,10,1),0);
        assertEquals(0,WeaponMath.smashKnockback(4,10,0),0);
    }
    @Test public void crystalDamageAndModernArmor() {
        assertEquals(85,WeaponMath.explosionDamage(0,1,6));
        assertEquals(32,WeaponMath.explosionDamage(.5,1,6));
        assertEquals(1,WeaponMath.explosionDamage(.5,0,6));
        assertEquals(0,WeaponMath.explosionDamage(1.01,1,6));
        assertEquals(8,WeaponMath.armor(20,20,8,0),1e-6);
        assertEquals(15.2,WeaponMath.armor(20,20,8,4),1e-6);
    }
    @Test public void materialSpecificSpearWindows() {
        assertEquals(15,SpearSpec.WOODEN.delay);
        assertEquals(8,SpearSpec.NETHERITE.delay);
        assertEquals(3,SpearSpec.IRON.jabDamage(),0);
    }
    @Test public void kineticSpeedThresholdsAndContactWindow() {
        SpearSpec s=SpearSpec.DIAMOND;
        assertFalse(s.damage(-1,20));
        assertFalse(s.damage(0,4.59));
        assertTrue(s.damage(s.damageDuration,4.6F));
        assertFalse(s.damage(s.damageDuration+1,20));
        assertEquals(11,s.chargeDamage(1,10),0);
        assertEquals(1,s.chargeDamage(1,-10),0);
    }
    @Test public void windBurstUsesLookupThenNativeFallback() {
        assertEquals(1.2,WeaponMath.windBurstMultiplier(1),0);
        assertEquals(2.2,WeaponMath.windBurstMultiplier(3),0);
        assertEquals(2.55,WeaponMath.windBurstMultiplier(4),1e-9);
    }
}
