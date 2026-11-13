package com.glowingfederal.moderncombatmovement;

import org.junit.Test;
import static org.junit.Assert.*;
import com.glowingfederal.moderncombatmovement.api.*;

public class CombatApiTest {
    @Test public void optInUsesExactId() {
        assertNull(CombatApi.find("foreign:sword"));
        ItemProperties properties = new ItemProperties(ItemProperties.Kind.SWORD, 1.6, 7, true, false);
        CombatApi.register("test:explicit_sword", properties);
        assertSame(properties, CombatApi.find("test:explicit_sword"));
        assertNull(CombatApi.find("other:explicit_sword"));
        assertFalse(properties.projectileOptIn);
    }
    @Test(expected=IllegalArgumentException.class) public void rejectNamespaceWildcard() {
        CombatApi.register("foreign:*", new ItemProperties(ItemProperties.Kind.SWORD, 1, 1, false, false));
    }
    @Test(expected=IllegalArgumentException.class) public void rejectInvalidSpeed() {
        new ItemProperties(ItemProperties.Kind.SWORD, Double.NaN, 1, false, false);
    }
    @Test public void legacyRegistryPathsRetainTheirCase() {
        ItemProperties properties = new ItemProperties(ItemProperties.Kind.SWORD, 1.6, 7, true, false);
        CombatApi.register("test:item.LegacySword", properties);
        assertSame(properties, CombatApi.find("test:item.LegacySword"));
        assertNull(CombatApi.find("test:item.legacysword"));
    }
}
