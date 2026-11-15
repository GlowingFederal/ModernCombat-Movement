package com.glowingfederal.moderncombatmovement.offhand;
/** Player-owned storage; no replacement of vanilla or foreign inventory indices. */
public interface OffhandAccess {
 OffhandInventory mcm$offhand();
 boolean mcm$offhandUsing();
 void mcm$offhandUsing(boolean value);
}
