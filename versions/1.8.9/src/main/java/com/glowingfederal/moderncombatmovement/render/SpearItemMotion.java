package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.glowingfederal.moderncombatmovement.compat.ModernEquipment;
public final class SpearItemMotion {
 private SpearItemMotion(){}
 public static void third(EntityPlayer p,ItemStack stack,int sign,float partial){
  com.glowingfederal.moderncombatmovement.combat.SpearSpec spec=ModernEquipment.spear(stack);if(spec==null||!ModernEquipment.enabled(p))return;
  boolean active=((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack()==stack;float swing=stack==com.glowingfederal.moderncombatmovement.combat.CombatHooks.held(p)?p.getSwingProgress(partial):0;
  SpearAnimationMath.thirdPerson(HandMatrices.INSTANCE,spec,sign,swing,active,p.getItemInUseDuration()+partial-1,com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).kineticHitTicks+partial);
 }
}
