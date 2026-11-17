package com.glowingfederal.moderncombatmovement.render;

/** Native shield arm placement followed by its left/right item display transform. */
public final class ShieldHandTransforms {
    private ShieldHandTransforms() {}
    public static void apply(CrossbowHandTransforms.Matrix m,int sign,float equip,float swing,boolean blocking) {
        if(blocking)m.translate(sign*.56F,-.52F-equip*.6F,-.72F);
        else CrossbowHandTransforms.apply(m,sign,equip,swing,false,0,1,false,false);
        float x=sign>0?(blocking?-15:-10):(blocking?5:10);
        m.translate(sign*x/16F,(blocking?5:sign>0?2:0)/16F,(blocking?-11:-10)/16F);
        m.rotate(sign*180,0,1,0);m.rotate(sign*(blocking?-5:5),0,0,1);m.scale(1.25F,1.25F,1.25F);
        m.translate(-.5,-.5,-.5);m.scale(1,-1,-1);
    }
}
