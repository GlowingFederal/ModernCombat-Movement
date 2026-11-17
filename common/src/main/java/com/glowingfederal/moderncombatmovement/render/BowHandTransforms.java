package com.glowingfederal.moderncombatmovement.render;
/** Native ItemInHandRenderer BOW branch, using the same matrix adapter as the other held weapons. */
public final class BowHandTransforms {
 private BowHandTransforms(){}
 public static void use(CrossbowHandTransforms.Matrix m,int sign,float equip,float elapsed){
  m.translate(sign*.56F,-.52F-equip*.6F,-.72F);
  m.translate(sign*-.2785682F,.18344387F,.15731531F);m.rotate(-13.935F,1,0,0);m.rotate(sign*35.3F,0,1,0);m.rotate(sign*-9.785F,0,0,1);
  float progress=Math.max(0,elapsed)/20;progress=Math.min(1,(progress*progress+progress*2)/3);
  if(progress>.1F)m.translate(0,Math.sin((elapsed-.1F)*1.3F)*(progress-.1F)*.004F,0);
  m.translate(0,0,progress*.04F);m.scale(1,1,1+progress*.2F);m.rotate(sign*-45F,0,1,0);
 }
}
