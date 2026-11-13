package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Native FoodData.tick correction. Existing replacement FoodStats and Modern Regen retain ownership. */
@Mixin(value=FoodStats.class,remap=false)
public abstract class FoodRegenMixin {
 @Shadow(aliases="field_75127_a")private int foodLevel;
 @Shadow(aliases="field_75125_b")private float foodSaturationLevel;
 @Shadow(aliases="field_75126_c")private float foodExhaustionLevel;
 @Shadow(aliases="field_75123_d")private int foodTimer;
 @Shadow(aliases="field_75124_e")private int prevFoodLevel;
 @Unique private static Boolean mcm$external;
 @Inject(method={"onUpdate","func_75118_a"},at=@At("HEAD"),cancellable=true)private void mcm$regen(EntityPlayer p,CallbackInfo ci){
  if(mcm$external==null)mcm$external=net.minecraftforge.fml.common.Loader.isModLoaded("modernregen");
  if(mcm$external||((Object)this).getClass()!=FoodStats.class||!com.glowingfederal.moderncombatmovement.config.Settings.combat(p.worldObj.isRemote))return;
  ci.cancel();prevFoodLevel=foodLevel;EnumDifficulty difficulty=p.worldObj.getDifficulty();
  if(foodExhaustionLevel>4){foodExhaustionLevel-=4;if(foodSaturationLevel>0)foodSaturationLevel=Math.max(0,foodSaturationLevel-1);else if(difficulty!=EnumDifficulty.PEACEFUL)foodLevel=Math.max(0,foodLevel-1);}
  boolean natural=p.worldObj.getGameRules().getBoolean("naturalRegeneration"),hurt=p.getHealth()>0&&p.getHealth()<p.getMaxHealth();
  if(natural&&foodSaturationLevel>0&&hurt&&foodLevel>=20){if(++foodTimer>=10){float amount=Math.min(foodSaturationLevel,6);p.heal(amount/6);foodExhaustionLevel=Math.min(foodExhaustionLevel+amount,40);foodTimer=0;}}
  else if(natural&&foodLevel>=18&&hurt){if(++foodTimer>=80){p.heal(1);foodExhaustionLevel=Math.min(foodExhaustionLevel+6,40);foodTimer=0;}}
  else if(foodLevel<=0){if(++foodTimer>=80){if(p.getHealth()>10||difficulty==EnumDifficulty.HARD||p.getHealth()>1&&difficulty==EnumDifficulty.NORMAL)p.attackEntityFrom(DamageSource.starve,1);foodTimer=0;}}
  else foodTimer=0;
 }
}
