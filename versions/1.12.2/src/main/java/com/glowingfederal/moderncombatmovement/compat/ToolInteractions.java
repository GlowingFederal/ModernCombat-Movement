package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.glowingfederal.moderncombatmovement.api.CombatApi;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
import com.glowingfederal.moderncombatmovement.config.Settings;
/** 26.3 BlockTransformer.playerHasBlockingItemUseIntent, scoped to opted-in tools. */
public final class ToolInteractions {
 private ToolInteractions(){}
 public static boolean yields(EntityPlayer p,ItemStack stack){
  return !stack.isEmpty()&&CombatApi.find(CombatHooks.id(stack))!=null&&!p.isSneaking()
   &&p.getHeldItemMainhand()==stack&&Settings.shields(p.world.isRemote)&&Shields.known(p.getHeldItemOffhand());
 }
}
