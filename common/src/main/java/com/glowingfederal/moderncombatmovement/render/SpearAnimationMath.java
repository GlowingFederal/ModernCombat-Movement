package com.glowingfederal.moderncombatmovement.render;

import com.glowingfederal.moderncombatmovement.combat.SpearSpec;

/** Vanilla 1.21.11 SpearAnimations timing, shared by native render adapters. */
public final class SpearAnimationMath {
    private SpearAnimationMath() {}
    public static float progress(float value,float start,float end) {
        return Math.max(0,Math.min(1,(value-start)/(end-start)));
    }
    public static float inQuad(float x) { return x*x; }
    public static float inOutSine(float x) { return (float)(-(Math.cos(Math.PI*x)-1)/2); }
    public static float inOutExpo(float x) {
        return x==0?0:x==1?1:x<.5F?(float)Math.pow(2,20*x-10)/2:(2-(float)Math.pow(2,-20*x+10))/2;
    }
    public static float outBack(float x) { float y=x-1;return 1+2.70158F*y*y*y+1.70158F*y*y; }
    public static float inOutBack(float x) {
        float c=1.70158F*1.525F;
        return x<.5F?(2*x)*(2*x)*((c+1)*2*x-c)/2:((2*x-2)*(2*x-2)*((c+1)*(2*x-2)+c)+2)/2;
    }
    private static float elastic(float x) {
        if(x==0||x==1)return x;
        float c=(float)(2*Math.PI/4.5);
        return x<.5F?-(float)(Math.pow(2,20*x-10)*Math.sin((20*x-11.125)*c))/2:
                (float)(Math.pow(2,-20*x+10)*Math.sin((20*x-11.125)*c))/2+1;
    }
    public static final class Use {
        public final float raise,start,middle,end,sway,lower,back,intensity,slow,fast;
        public Use(SpearSpec spec,float ticks) {
            int dismount=spec.delay+spec.dismountDuration,knockback=spec.delay+spec.knockbackDuration;
            int damage=spec.delay+spec.damageDuration;
            raise=progress(ticks,0,spec.delay);start=progress(raise,0,.5F);
            middle=progress(raise,.5F,.8F);end=progress(raise,.8F,1);
            sway=progress(ticks,dismount-20,knockback-40);
            float e=elastic(progress(ticks-20,knockback-40,knockback));lower=1-(1-e)*(1-e)*(1-e);
            back=progress(ticks,damage-5,damage);
            intensity=2*(float)Math.sqrt(1-(sway-1)*(sway-1))-2*(1-(float)Math.sqrt(1-back*back));
            slow=(float)Math.sin(ticks*19*(float)(Math.PI/180))*intensity;
            fast=(float)Math.sin(ticks*30*(float)(Math.PI/180))*intensity;
        }
    }
    public static void firstPerson(CrossbowHandTransforms.Matrix m,SpearSpec spec,int sign,float equip,float swing,float using) {
        m.translate(sign*.56F,-.52F+equip*-.6F,-.72F);
        float a=inOutSine(progress(swing,0,.05F)),b=outBack(progress(swing,.05F,.2F)),c=inOutExpo(progress(swing,.4F,1));
        m.translate(sign*.1F*(a-b),-.075F*(a-c),.65F*(a-b));
        m.rotate(-70*(a-c),1,0,0);m.translate(0,0,-.25*(c-b));
        if(using<=0)return;
        Use u=new Use(spec,using);
        m.translate(sign*(u.raise*.15F+u.end*-.05F+u.sway*-.1F+u.slow*.005F),
                u.raise*-.075F+u.middle*.075F+u.fast*.01F,u.start*.05+u.end*-.05+u.slow*.005F);
        m.translate(0,.1F,0);m.rotate(-65*inOutBack(u.raise)-35*u.lower+100*u.back-.5F*u.fast,1,0,0);m.translate(0,-.1F,0);
        m.translate(sign*.15F,0,0);m.rotate(sign*(-90*progress(u.raise,.5F,.55F)+90*u.sway+2*u.slow),0,-1,0);m.translate(-sign*.15F,0,0);
    }
}
