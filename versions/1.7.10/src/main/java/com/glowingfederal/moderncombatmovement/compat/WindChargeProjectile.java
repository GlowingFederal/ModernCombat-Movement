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
	@Override protected float func_70182_d() { return 1.5F; }
    @Override public void onUpdate(){double x=motionX,y=motionY,z=motionZ;super.onUpdate();if(!isDead){motionX=x;motionY=y;motionZ=z;}if(!worldObj.isRemote&&posY>worldObj.getHeight()+30){com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(worldObj,getThrower(),posX,posY,posZ);setDead();}}
    public void launch(EntityLivingBase shooter){
        net.minecraft.util.Vec3 look=shooter.getLookVec();motionX=(look.xCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionY=(look.yCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;motionZ=(look.zCoord+(rand.nextDouble()-rand.nextDouble())*.0172275)*1.5;
        setPosition(shooter.posX,shooter.boundingBox.minY+shooter.getEyeHeight(),shooter.posZ);rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
    }
    @Override protected void onImpact(MovingObjectPosition hit){
        if(worldObj.isRemote)return;
        if(hit.entityHit instanceof WindChargeProjectile||hit.entityHit instanceof net.minecraft.entity.item.EntityEnderCrystal)return;
        if(hit.entityHit!=null)hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this,getThrower()),1);
        double x=posX,y=posY,z=posZ;
        if(hit.typeOfHit==MovingObjectPosition.MovingObjectType.BLOCK){x=hit.hitVec.xCoord;y=hit.hitVec.yCoord;z=hit.hitVec.zCoord;x+=net.minecraft.util.Facing.offsetsXForSide[hit.sideHit]*.25;y+=net.minecraft.util.Facing.offsetsYForSide[hit.sideHit]*.25;z+=net.minecraft.util.Facing.offsetsZForSide[hit.sideHit]*.25;}
        com.glowingfederal.moderncombatmovement.combat.CombatBlast.charge(worldObj,getThrower(),x,y,z);setDead();
    }
}
