package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.world.World;
/** Anchor callback boundary; all owned explosion calculations have one canonical implementation. */
public final class AnchorBlast {private AnchorBlast(){} public static void explode(World w,int x,int y,int z,boolean wet){com.glowingfederal.moderncombatmovement.combat.CombatBlast.anchor(w,x,y,z,wet);}}
