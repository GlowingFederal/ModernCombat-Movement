/* Adapted from UAB EntityWindCharge; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
public class WindChargeProjectile extends EntityThrowable {
    private EntityLivingBase deflector;
    private int lastDeflect=-100;
    @Override public EntityLivingBase getThrower(){return deflector==null?super.getThrower():deflector;}
    @Override public boolean canBeCollidedWith(){return true;}
    @Override public boolean attackEntityFrom(DamageSource source,float amount){Entity e=source.getTrueSource();if(ticksExisted<5||ticksExisted-lastDeflect<5||!(e instanceof EntityLivingBase))return false;deflector=(EntityLivingBase)e;lastDeflect=ticksExisted;launchDirection(deflector);velocityChanged=true;return true;}
    private void launchDirection(EntityLivingBase shooter){net.minecraft.util.math.Vec3d look=shooter.getLookVec();motionX=look.x*1.5;motionY=look.y*1.5;motionZ=look.z*1.5;}

    public WindChargeProjectile(World world){super(world);}
    public WindChargeProjectile(World world,EntityLivingBase shooter){super(world,shooter);}
    @Override protected float getGravityVelocity(){return 0;}
    @Override public void onUpdate(){double x=motionX,y=motionY,z=motionZ;super.onUpdate();if(world.isRemote&&!isDead&&ticksExisted%2==0)com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.windTrail(world,posX,posY,posZ);if(!isDead){motionX=x;motionY=y;motionZ=z;}if(!world.isRemote&&posY>world.getHeight()+30){com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(world,getThrower(),posX,posY,posZ);setDead();}}
    public void launch(EntityLivingBase shooter){
        net.minecraft.util.math.Vec3d look=shooter.getLookVec();motionX=(look.x+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionY=(look.y+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionZ=(look.z+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;
        setPosition(shooter.posX,shooter.getEntityBoundingBox().minY+shooter.getEyeHeight(),shooter.posZ);rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
    }
    @Override protected void onImpact(RayTraceResult hit){
        if(world.isRemote)return;
        if(hit.entityHit instanceof WindChargeProjectile||hit.entityHit instanceof net.minecraft.entity.item.EntityEnderCrystal)return;
        if(hit.entityHit!=null)hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this,getThrower()),1);
        double x=posX,y=posY,z=posZ;
        if(hit.typeOfHit==RayTraceResult.Type.BLOCK){ProjectileBlocks.hit(world,getThrower(),hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ());x=hit.hitVec.x;y=hit.hitVec.y;z=hit.hitVec.z;x+=hit.sideHit.getXOffset()*.25;y+=hit.sideHit.getYOffset()*.25;z+=hit.sideHit.getZOffset()*.25;}
        com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(world,getThrower(),x,y,z);setDead();
    }
}
