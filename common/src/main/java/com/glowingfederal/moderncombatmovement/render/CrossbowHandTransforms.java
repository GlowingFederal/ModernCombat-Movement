package com.glowingfederal.moderncombatmovement.render;

/** ItemInHandRenderer's crossbow branch in 1.21.11; native backends implement only the matrix operations. */
public final class CrossbowHandTransforms {
    public interface Matrix {
        void translate(double x,double y,double z);
        void rotate(float degrees,float x,float y,float z);
        void scale(float x,float y,float z);
    }
    private CrossbowHandTransforms() {}
    public static void apply(Matrix m,int sign,float equip,float swing,boolean charging,
            float elapsed,float duration,boolean charged,boolean mainHand) {
        m.translate(sign*.56F,-.52F+equip*-.6F,-.72F);
        if(charging) {
            m.translate(sign*-.4785682F,-.094387F,.05731531F);
            m.rotate(-11.935F,1,0,0);m.rotate(sign*65.3F,0,1,0);m.rotate(sign*-9.785F,0,0,1);
            float progress=Math.min(1,elapsed/duration);
            if(progress>.1F)m.translate(0,Math.sin((elapsed-.1F)*1.3F)*(progress-.1F)*.004F,0);
            m.translate(0,0,progress*.04F);m.scale(1,1,1+progress*.2F);m.rotate(sign*-45F,0,1,0);
        } else {
            float root=(float)Math.sqrt(swing);
            m.translate(sign*-.4F*(float)Math.sin(root*(float)Math.PI),
                    .2F*(float)Math.sin(root*(float)(Math.PI*2)),
                    -.2F*(float)Math.sin(swing*(float)Math.PI));
            float squared=(float)Math.sin(swing*swing*(float)Math.PI), sine=(float)Math.sin(root*(float)Math.PI);
            m.rotate(sign*(45+squared*-20),0,1,0);m.rotate(sign*sine*-20,0,0,1);
            m.rotate(sine*-80,1,0,0);m.rotate(sign*-45,0,1,0);
            if(charged && swing<.001F && mainHand) {m.translate(sign*-.641864F,0,0);m.rotate(sign*10,0,1,0);}
        }
    }
}
