package com.glowingfederal.moderncombatmovement.render;

/** Vanilla ElytraAnimationState runs once per entity tick, independently of frame rate. */
public final class WingAnimation {
    public float x,y,z,previousX,previousY,previousZ;
    public void tick(boolean flying,boolean crouching,double vx,double vy,double vz) {
        previousX=x;previousY=y;previousZ=z;
        float targetX,targetY,targetZ;
        if(flying) {
            float amount=1;
            double length=Math.sqrt(vx*vx+vy*vy+vz*vz);
            if(vy<0&&length>=1e-4)amount=1-(float)Math.pow(-vy/length,1.5);
            targetX=(float)(Math.PI/12)+amount*((float)(Math.PI/9)-(float)(Math.PI/12));
            targetZ=(float)(-Math.PI/12)+amount*((float)(-Math.PI/2)-(float)(-Math.PI/12));
            targetY=0;
        } else if(crouching) {targetX=(float)(Math.PI*2/9);targetY=.08726646F;targetZ=(float)(-Math.PI/4);}
        else {targetX=(float)(Math.PI/12);targetY=0;targetZ=(float)(-Math.PI/12);}
        x+=(targetX-x)*.3F;y+=(targetY-y)*.3F;z+=(targetZ-z)*.3F;
    }
    public float x(float partial){return previousX+(x-previousX)*partial;}
    public float y(float partial){return previousY+(y-previousY)*partial;}
    public float z(float partial){return previousZ+(z-previousZ)*partial;}
}
