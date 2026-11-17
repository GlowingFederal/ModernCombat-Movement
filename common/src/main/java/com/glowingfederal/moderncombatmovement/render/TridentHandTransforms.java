package com.glowingfederal.moderncombatmovement.render;

/** ItemInHandRenderer's native trident charging and auto-spin arm transforms. */
public final class TridentHandTransforms {
    private TridentHandTransforms() {}
    public static void apply(CrossbowHandTransforms.Matrix m,int sign,float equip,float swing,
            boolean charging,float elapsed,boolean spinning) {
        if(!charging&&!spinning) {
            CrossbowHandTransforms.apply(m,sign,equip,swing,false,0,1,false,false);
            return;
        }
        m.translate(sign*.56F,-.52F-equip*.6F,-.72F);
        if(charging) {
            m.translate(sign*-.5F,.7F,.1F);
            m.rotate(-55,1,0,0);m.rotate(sign*35.3F,0,1,0);m.rotate(sign*-9.785F,0,0,1);
            float progress=Math.min(1,elapsed/10);
            if(progress>.1F)m.translate(0,Math.sin((elapsed-.1F)*1.3F)*(progress-.1F)*.004F,0);
            m.translate(0,0,progress*.2F);m.scale(1,1,1+progress*.2F);m.rotate(sign*-45,0,1,0);
        } else {
            m.translate(sign*-.4F,.8F,.3F);m.rotate(sign*65,0,1,0);m.rotate(sign*-85,0,0,1);
        }
    }
}
