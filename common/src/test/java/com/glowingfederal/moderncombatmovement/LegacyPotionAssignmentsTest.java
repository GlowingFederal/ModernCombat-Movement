package com.glowingfederal.moderncombatmovement;

import com.glowingfederal.moderncombatmovement.compat.LegacyPotionAssignments;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

public class LegacyPotionAssignmentsTest {
    private static final String OWNER = "moderncombatmovement";
    private static final String[] LEGACY = {"weaving", "oozing", "infested", "wind_charged"};

    @Test public void retrogradeKeepsUabGlowingAndAllocatesImportedWindCharged() {
        Properties saved = new Properties();
        saved.setProperty("etfuturum:glowing", "28");
        assertEquals(-1, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", 28, 28));
        assertEquals("28", saved.getProperty("etfuturum:glowing"));
    }

    @Test public void savedMcmIdsAndDeliberateOverridesAreNeverSilentlyReassigned() {
        Properties saved = new Properties();
        saved.setProperty("etfuturum:glowing", "28");
        saved.setProperty(OWNER + ":wind_charged", "28");
        assertEquals(28, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", 28, 28));
        saved.remove(OWNER + ":wind_charged");
        assertEquals(28, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", 28, 27));
        assertEquals(45, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", 45, 28));
    }

    @Test public void compatibleLegacyAndAutomaticSettingsRetainTheirMeaning() {
        Properties saved = new Properties();
        saved.setProperty("schema", "1");
        assertEquals(28, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", 28, 28));
        assertEquals(-1, LegacyPotionAssignments.request(saved, OWNER, "wind_charged", -1, 28));
    }

    @Test public void changedDefaultsBlockAmbiguousOldMcmWorldsButAllowUabAndCompleteSnapshots() {
        Properties saved = new Properties();
        saved.setProperty("etfuturum:glowing", "28");
        assertFalse(LegacyPotionAssignments.ambiguousWorld(saved, OWNER, LEGACY));
        saved.setProperty(OWNER + ":slow_falling", "34");
        assertTrue(LegacyPotionAssignments.ambiguousWorld(saved, OWNER, LEGACY));
        for (int i = 0; i < LEGACY.length; i++) saved.setProperty(OWNER + ":" + LEGACY[i], Integer.toString(35 + i));
        assertFalse(LegacyPotionAssignments.ambiguousWorld(saved, OWNER, LEGACY));
    }
}
