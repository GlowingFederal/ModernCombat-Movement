/* Adapted from UAB EntityLingeringEffect; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;


import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public class LingeringCloud extends Entity implements IEntityAdditionalSpawnData {

	private static final int TICKS_DATA_WATCHER = 10, WIDTH_DATA_WATCHER = 11, HEIGHT_DATA_WATCHER = 12;

	private EntityLivingBase thrower;
	private ItemStack stack;
	private final int MAX_TICKS = 30 * 20;
	private int colorOverride;
    private final java.util.Map<java.util.UUID,Integer> victims=new java.util.HashMap<java.util.UUID,Integer>();
    private java.util.UUID owner;
    private float radius=3;
    private static final int WAIT=10;

	public LingeringCloud(World world) {
		super(world);

		yOffset = 0;
		setSize(6, .5F);
	}

	public LingeringCloud(World world, LingeringProjectile potion) {
		this(world, potion.getStack(), potion.getThrower());
		setPosition(potion.posX, potion.posY, potion.posZ);
	}

	public LingeringCloud(World world, ItemStack stack, EntityLivingBase thrower) {
		this(world);
		this.stack = stack;
		this.thrower = thrower;this.owner=thrower==null?null:thrower.getUniqueID();
	}

	public void setColorOverride(int color) {
		colorOverride = color;
	}

	@Override
	public boolean canBePushed() {
        return false;
	}

	@Override
	public void applyEntityCollision(Entity e) {
        // Modern clouds apply from their server tick; physical collisions never deliver effects.
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(TICKS_DATA_WATCHER, 0);
		dataWatcher.addObject(WIDTH_DATA_WATCHER, 6.0F);
		dataWatcher.addObject(HEIGHT_DATA_WATCHER, 0.5F);
	}

	@Override
	public void onUpdate() {
        super.onUpdate();
        if(worldObj.isRemote){
            float tracked=dataWatcher.getWatchableObjectFloat(WIDTH_DATA_WATCHER);if(width!=tracked)setSize(tracked,.5F);
            if(stack==null)return;int colour=colorOverride>0?colorOverride:net.minecraft.potion.PotionHelper.calcPotionLiquidColor(PotionDelivery.effects(stack));
            float r=(colour>>16&255)/255F,g=(colour>>8&255)/255F,b=(colour&255)/255F;
            double particleRadius=dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER)<WAIT?.2:width/2;int particles=Math.max(1,(int)Math.ceil(Math.PI*particleRadius*particleRadius));
            for(int i=0;i<particles;i++){double angle=rand.nextDouble()*Math.PI*2,distance=Math.sqrt(rand.nextDouble())*particleRadius;worldObj.spawnParticle("mobSpell",posX+Math.cos(angle)*distance,posY,posZ+Math.sin(angle)*distance,r,g,b);}return;
        }
        int age=dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER)+1;dataWatcher.updateObject(TICKS_DATA_WATCHER,age);
        if(stack==null||age>=WAIT+MAX_TICKS){setDead();return;}if(age<WAIT)return;
        radius-=3F/MAX_TICKS;if(radius<.5F){setDead();return;}setSize(radius*2,.5F);
        if(age%5!=0||!com.glowingfederal.moderncombatmovement.config.Settings.kit(false))return;
        java.util.Iterator<java.util.Map.Entry<java.util.UUID,Integer>> it=victims.entrySet().iterator();while(it.hasNext())if(age>=it.next().getValue())it.remove();
        List<PotionEffect> effects=PotionDelivery.effects(stack);if(effects.isEmpty()){victims.clear();return;}
        List<EntityLivingBase> entities=worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox);
        for(EntityLivingBase target:entities){
            if(victims.containsKey(target.getUniqueID())||!target.isEntityAlive())continue;
            double dx=target.posX-posX,dz=target.posZ-posZ;if(dx*dx+dz*dz>radius*radius)continue;
            boolean eligible=false;for(PotionEffect effect:effects)if(target.isPotionApplicable(effect)){eligible=true;break;}if(!eligible)continue;
            victims.put(target.getUniqueID(),age+20);
            for(PotionEffect effect:effects){Potion potion=Potion.potionTypes[effect.getPotionID()];if(potion==null)continue;if(potion.isInstant()){potion.affectEntity(resolveOwner(),target,effect.getAmplifier(),.5);}else target.addPotionEffect(new PotionEffect(effect.getPotionID(),Math.max(1,effect.getDuration()/4),effect.getAmplifier(),effect.getIsAmbient()));}
            radius-=.5F;if(radius<.5F){setDead();return;}setSize(radius*2,.5F);
        }
	}

	@Override
	public void moveEntity(double x, double y, double z) {
	}

	@Override
	public void addVelocity(double x, double y, double z) {
	}

	@Override
	protected void setSize(float width, float height) {
		super.setSize(width, height);
		dataWatcher.updateObject(WIDTH_DATA_WATCHER, this.width);
		dataWatcher.updateObject(HEIGHT_DATA_WATCHER, this.height);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, stack);
		buffer.writeInt(colorOverride);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		stack = ByteBufUtils.readItemStack(buffer);
		colorOverride = buffer.readInt();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Potion"));
		if (stack == null) {
			setDead();
		} else {
			dataWatcher.updateObject(TICKS_DATA_WATCHER,nbt.getInteger("Ticks"));radius=nbt.hasKey("Radius")?Math.max(0,Math.min(3,nbt.getFloat("Radius"))):Math.max(0,3*(1-nbt.getInteger("Ticks")/(float)MAX_TICKS));setSize(radius*2,.5F);if(nbt.hasKey("Owner"))try{owner=java.util.UUID.fromString(nbt.getString("Owner"));}catch(IllegalArgumentException ignored){}
			if (nbt.hasKey("colorOverride")) {
				colorOverride = nbt.getInteger("colorOverride");
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if (stack != null) {
			nbt.setTag("Potion", stack.writeToNBT(new NBTTagCompound()));nbt.setFloat("Radius",radius);if(owner!=null)nbt.setString("Owner",owner.toString());
			nbt.setInteger("Ticks", dataWatcher.getWatchableObjectInt(TICKS_DATA_WATCHER));
			if (colorOverride > 0) {
				nbt.setInteger("colorOverride", colorOverride);
			}
		}
	}

    private EntityLivingBase resolveOwner(){if(thrower!=null&&!thrower.isDead)return thrower;if(owner!=null)for(Object value:worldObj.loadedEntityList)if(value instanceof EntityLivingBase&&owner.equals(((EntityLivingBase)value).getUniqueID()))return thrower=(EntityLivingBase)value;return null;}
    public float radius(){return radius;}
    public ItemStack getStack() {
		return stack;
	}

	public EntityLivingBase getThrower() {
		return thrower;
	}
}