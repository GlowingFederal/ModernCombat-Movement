package com.glowingfederal.moderncombatmovement.combat;
import net.minecraft.item.ItemStack;
/** Dedicated-safe access: Mojang strips the public legacy use-item getters from servers. */
public interface PlayerUseAccess { ItemStack mcm$useStack(); int mcm$useRemaining(); }
