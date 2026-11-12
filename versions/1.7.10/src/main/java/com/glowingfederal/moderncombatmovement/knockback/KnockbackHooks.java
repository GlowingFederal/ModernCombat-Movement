package com.glowingfederal.moderncombatmovement.knockback;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
public final class KnockbackHooks {
 private KnockbackHooks() {}
 public static void apply(EntityLivingBase p,Entity attacker,float strength,double x,double z) {
  
  while(x*x+z*z<1.0E-5F) { x=(p.getRNG().nextDouble()-p.getRNG().nextDouble())*.01; z=(p.getRNG().nextDouble()-p.getRNG().nextDouble())*.01; }
  double resistance=p.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
  if(strength*(1-resistance)<=0) return;
  double[] velocity=KnockbackMath.apply(p.motionX,p.motionY,p.motionZ,strength,resistance,x,z,p.onGround);
  p.motionX=velocity[0]; p.motionY=velocity[1]; p.motionZ=velocity[2]; p.isAirBorne=true;
 }
}
