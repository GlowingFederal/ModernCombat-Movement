package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
public final class Gust {private Gust(){} public static void burst(World w,Entity source,double x,double y,double z,float radius){com.glowingfederal.moderncombatmovement.combat.CombatBlast.wind(w,source,x,y,z,radius,1);}}
