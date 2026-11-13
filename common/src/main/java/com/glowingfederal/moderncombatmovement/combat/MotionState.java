package com.glowingfederal.moderncombatmovement.combat;

/** Native Entity.computeSpeed semantics: resolved positional movement, rather than residual motion fields. */
public final class MotionState {
    private boolean initialized;
    private int dimension;
    private double previousX,previousY,previousZ;
    public double x,y,z;
    public void sample(int dimension,double px,double py,double pz) {
        if(initialized && this.dimension==dimension) {
            x=px-previousX;y=py-previousY;z=pz-previousZ;
        } else { x=0;y=0;z=0; }
        initialized=true;this.dimension=dimension;previousX=px;previousY=py;previousZ=pz;
    }
    public void reset(int dimension,double px,double py,double pz) {
        initialized=false;sample(dimension,px,py,pz);
    }
}
