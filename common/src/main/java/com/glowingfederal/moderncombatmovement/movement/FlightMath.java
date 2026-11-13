package com.glowingfederal.moderncombatmovement.movement;

/** LivingEntity.updateFallFlyingMovement and FireworkRocketEntity.tick, vanilla 1.21.11. */
public final class FlightMath {
    private FlightMath() {}
    public static void travel(FlightMotion velocity,double lookX,double lookZ,float pitch,float sinPitch,double gravity) {
        double lookHorizontal=Math.sqrt(lookX*lookX+lookZ*lookZ);
        double horizontal=Math.sqrt(velocity.x*velocity.x+velocity.z*velocity.z);
        double lift=Math.cos(pitch);lift*=lift;
        velocity.y+=gravity*(-1+lift*.75);
        if(velocity.y<0&&lookHorizontal>0) {
            double rise=velocity.y*-.1*lift;
            velocity.x+=lookX*rise/lookHorizontal;velocity.y+=rise;velocity.z+=lookZ*rise/lookHorizontal;
        }
        if(pitch<0&&lookHorizontal>0) {
            double turn=horizontal*-sinPitch*.04;
            velocity.x-=lookX*turn/lookHorizontal;velocity.y+=turn*3.2;velocity.z-=lookZ*turn/lookHorizontal;
        }
        if(lookHorizontal>0) {
            velocity.x+=(lookX/lookHorizontal*horizontal-velocity.x)*.1;
            velocity.z+=(lookZ/lookHorizontal*horizontal-velocity.z)*.1;
        }
        velocity.x*=.99F;velocity.y*=.98F;velocity.z*=.99F;
    }
    public static void boost(FlightMotion velocity,double lookX,double lookY,double lookZ) {
        velocity.x+=lookX*.1+(lookX*1.5-velocity.x)*.5;
        velocity.y+=lookY*.1+(lookY*1.5-velocity.y)*.5;
        velocity.z+=lookZ*.1+(lookZ*1.5-velocity.z)*.5;
    }
    public static float wallDamage(double before,double after) { return (float)((before-after)*10-3); }
}
