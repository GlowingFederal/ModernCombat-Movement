/* Adapted from UAB EntityTrident; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/** The thrown stack is authoritative and is transferred exactly once on impact. */
public class TridentProjectile extends EntityThrowable {
	private ItemStack stack;
	private boolean returning,embedded,dealtDamage,creative;
    private int groundTicks;
	@Override protected void entityInit() { super.entityInit(); dataWatcher.addObject(16, (byte) 0); }
	private java.util.UUID owner;
	public TridentProjectile(World world) { super(world); }
	public TridentProjectile(World world, EntityLivingBase shooter, ItemStack stack) { super(world, shooter); owner = shooter.getUniqueID(); this.stack = stack == null ? null : stack.copy(); if (this.stack != null) this.stack.stackSize = 1; }
	@Override protected float func_70182_d() { return 2.5F; }
	@Override protected float getGravityVelocity() { return .05F; }
	@Override protected void onImpact(MovingObjectPosition hit) {
		if (worldObj.isRemote) return;
		if (returning || dealtDamage) return;
		if (hit.entityHit != null) {
			int impaling = Tridents.level(Tridents.impaling, stack);
			boolean aquatic = hit.entityHit instanceof net.minecraft.entity.passive.EntityWaterMob || hit.entityHit.getClass().getName().equals("ganymedes01.etfuturum.entities.EntityGuardian");
			hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 8 + (aquatic ? impaling * 2.5F : 0));
			if (worldObj.isThundering() && Tridents.level(Tridents.channeling, stack) > 0 && worldObj.canBlockSeeTheSky(net.minecraft.util.MathHelper.floor_double(hit.entityHit.posX), net.minecraft.util.MathHelper.floor_double(hit.entityHit.posY), net.minecraft.util.MathHelper.floor_double(hit.entityHit.posZ))) worldObj.addWeatherEffect(new net.minecraft.entity.effect.EntityLightningBolt(worldObj, hit.entityHit.posX, hit.entityHit.posY, hit.entityHit.posZ));
		}

        if(hit.entityHit!=null){dealtDamage=true;motionX*=-.01;motionY*=-.1;motionZ*=-.01;}
        else{embedded=true;setPosition(hit.hitVec.xCoord,hit.hitVec.yCoord,hit.hitVec.zCoord);motionX=motionY=motionZ=0;}
        if(dealtDamage&&Tridents.level(Tridents.loyalty,stack)>0){returning=true;dataWatcher.updateObject(16, (byte) 1); noClip=true;}
    }
    public void creative(boolean value){creative=value;}
    @Override public void onCollideWithPlayer(EntityPlayer player){
        if(worldObj.isRemote||!embedded&&!returning||owner!=null&&!owner.equals(player.getUniqueID())||stack==null)return;
        if(creative){if(player.capabilities.isCreativeMode){stack=null;setDead();}return;}
        if(player.inventory.addItemStackToInventory(stack)){stack=null;setDead();}
    }

	@Override public void onUpdate() {
		if (worldObj.isRemote && dataWatcher.getWatchableObjectByte(16) != 0) { onEntityUpdate(); return; }
		if (embedded && !returning) { onEntityUpdate();groundTicks++;if(groundTicks>4&&Tridents.level(Tridents.loyalty,stack)>0){returning=true;dataWatcher.updateObject(16, (byte) 1);noClip=true;}if(!worldObj.isRemote&&groundTicks>1200)setDead();return; }
        if (worldObj.isRemote || !returning) { super.onUpdate(); if(!returning&&!embedded&&isInWater()){motionX*=.99/.8;motionY=(motionY+.05)*(.99/.8)-.05;motionZ*=.99/.8;} if (!worldObj.isRemote && ticksExisted > 1200) { if (stack != null) { if(!creative)entityDropItem(stack, 0); stack = null; } setDead(); } return; }
		onEntityUpdate(); EntityPlayer player = owner == null ? null : worldObj.func_152378_a(owner);
		if (player == null || !player.isEntityAlive() || player.dimension != dimension) { if (stack != null) { if(!creative)entityDropItem(stack, 0); stack = null; } setDead(); return; }
		double dx = player.posX - posX, dy = player.posY + player.getEyeHeight() - posY, dz = player.posZ - posZ, length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length < 1.5) { if(creative){stack=null;setDead();return;} if (stack != null && player.inventory.addItemStackToInventory(stack)) { stack = null; setDead(); }  return; }
		int loyalty=Tridents.level(Tridents.loyalty,stack);posY+=dy*.015*loyalty;double speed=.05*loyalty;motionX=motionX*.95+dx/length*speed;motionY=motionY*.95+dy/length*speed;motionZ=motionZ*.95+dz/length*speed; setPosition(posX + motionX, posY + motionY, posZ + motionZ);
	}
	@Override public void writeEntityToNBT(NBTTagCompound tag) { super.writeEntityToNBT(tag); if (stack != null) tag.setTag("Trident", stack.writeToNBT(new NBTTagCompound())); tag.setBoolean("Returning", returning);tag.setBoolean("Embedded",embedded);tag.setBoolean("DealtDamage",dealtDamage);tag.setBoolean("Creative",creative); if (owner != null) tag.setString("OwnerUUID", owner.toString()); }
	@Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag); stack = tag.hasKey("Trident", 10) ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("Trident")) : null; if (stack != null && !Tridents.known(stack)) stack = null; embedded=tag.getBoolean("Embedded");dealtDamage=tag.getBoolean("DealtDamage");creative=tag.getBoolean("Creative");returning = tag.getBoolean("Returning"); noClip = returning; dataWatcher.updateObject(16, (byte) (returning ? 1 : 0)); try { owner = java.util.UUID.fromString(tag.getString("OwnerUUID")); } catch (IllegalArgumentException ignored) { owner = null; } }
}
