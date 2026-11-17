/* Adapted from UAB EntityWindCharge; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.*;
import net.minecraft.world.World;
public class WindChargeProjectile extends EntityThrowable {
    private EntityLivingBase deflector;
    private int lastDeflect=-100;
    @Override public EntityLivingBase getThrower(){return deflector==null?super.getThrower():deflector;}
    @Override public boolean canBeCollidedWith(){return true;}
    @Override public boolean attackEntityFrom(DamageSource source,float amount){Entity e=source.getEntity();if(ticksExisted<5||ticksExisted-lastDeflect<5||!(e instanceof EntityLivingBase))return false;deflector=(EntityLivingBase)e;lastDeflect=ticksExisted;launchDirection(deflector);velocityChanged=true;return true;}
    private void launchDirection(EntityLivingBase shooter){net.minecraft.util.Vec3 look=shooter.getLookVec();motionX=look.xCoord*1.5;motionY=look.yCoord*1.5;motionZ=look.zCoord*1.5;}

	public WindChargeProjectile(World world) { super(world); }
	public WindChargeProjectile(World world, EntityLivingBase shooter) { super(world, shooter); }
	@Override protected float getGravityVelocity() { return 0; }
	@Override protected float getVelocity() { return 1.5F; }
    @Override public void onUpdate(){double x=motionX,y=motionY,z=motionZ;super.onUpdate();if(worldObj.isRemote&&!isDead&&ticksExisted%2==0)com.glowingfederal.moderncombatmovement.ModernCombatMovement.proxy.windTrail(worldObj,posX,posY,posZ);if(!isDead){motionX=x;motionY=y;motionZ=z;}if(!worldObj.isRemote&&posY>worldObj.getHeight()+30){com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(worldObj,getThrower(),posX,posY,posZ);setDead();}}
    public void launch(EntityLivingBase shooter){
        net.minecraft.util.Vec3 look=shooter.getLookVec();motionX=(look.xCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionY=(look.yCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionZ=(look.zCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;
        setPosition(shooter.posX,shooter.getEntityBoundingBox().minY+shooter.getEyeHeight(),shooter.posZ);rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
    }
    @Override protected void onImpact(MovingObjectPosition hit){
        if(worldObj.isRemote)return;
        if(hit.entityHit instanceof WindChargeProjectile||hit.entityHit instanceof net.minecraft.entity.item.EntityEnderCrystal)return;
        if(hit.entityHit!=null)hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this,getThrower()),1);
        double x=posX,y=posY,z=posZ;
        if(hit.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK){ProjectileBlocks.hit(worldObj,getThrower(),hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ());x=hit.hitVec.xCoord;y=hit.hitVec.yCoord;z=hit.hitVec.zCoord;x+=hit.sideHit.getFrontOffsetX()*.25;y+=hit.sideHit.getFrontOffsetY()*.25;z+=hit.sideHit.getFrontOffsetZ()*.25;}
        com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(worldObj,getThrower(),x,y,z);setDead();
    }
}
