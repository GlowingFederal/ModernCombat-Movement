package com.glowingfederal.moderncombatmovement.render;
import org.lwjgl.opengl.GL11;
public final class HandMatrices implements CrossbowHandTransforms.Matrix {
 public static final HandMatrices INSTANCE=new HandMatrices();private HandMatrices(){}
 public void translate(double x,double y,double z){GL11.glTranslated(x,y,z);}
 public void rotate(float angle,float x,float y,float z){GL11.glRotatef(angle,x,y,z);}
 public void scale(float x,float y,float z){GL11.glScalef(x,y,z);}
}
