package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.enchantment.Enchantment;
/** Selected modern components, applied to the reused registered enchantments. */
public final class ProjectileEnchantments {
 private ProjectileEnchantments(){}
 public static int minimum(Enchantment e,int level){
  if(!active())return -1;
  if(e==Tridents.loyalty)return 12+(level-1)*7;if(e==Tridents.riptide)return 17+(level-1)*7;if(e==Tridents.impaling)return 1+(level-1)*8;if(e==Tridents.channeling)return 25;
  if(e==Crossbows.quickCharge)return 12+(level-1)*20;if(e==Crossbows.multishot)return 20;if(e==Crossbows.piercing)return 1+(level-1)*10;return -1;
 }
 public static int maximum(Enchantment e,int level){if(minimum(e,level)<0)return -1;return e==Tridents.impaling?21+(level-1)*8:50;}
 public static int weight(Enchantment e){
  if(!active())return -1;
  if(e==Tridents.loyalty||e==Crossbows.quickCharge)return 5;if(e==Crossbows.piercing)return 10;if(e==Tridents.channeling)return 1;
  if(e==Tridents.impaling||e==Tridents.riptide||e==Crossbows.multishot)return 2;return -1;
 }
 private static boolean active(){return com.glowingfederal.moderncombatmovement.config.Settings.projectiles(cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient());}
}
