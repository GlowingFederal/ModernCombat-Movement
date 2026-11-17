/* Adapted from UAB EntityLingeringPotion; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class LingeringProjectile extends EntityPotion implements IEntityAdditionalSpawnData {

	private ItemStack stack;

	public LingeringProjectile(World world) {
		super(world);
	}

	public LingeringProjectile(World world, EntityLivingBase thrower, ItemStack stack) {
		super(world, thrower, stack);
		this.stack = stack==null?null:stack.copy();if(this.stack!=null)this.stack.stackSize=1;
	}

	public LingeringProjectile(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
		this.stack = stack==null?null:stack.copy();if(this.stack!=null)this.stack.stackSize=1;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) {
        if(worldObj.isRemote)return;
        if(stack!=null&&com.glowingfederal.moderncombatmovement.config.Settings.kit(false)){LingeringCloud cloud=new LingeringCloud(worldObj,this);if(mop.entityHit!=null)cloud.setPosition(mop.entityHit.posX,mop.entityHit.posY,mop.entityHit.posZ);worldObj.spawnEntityInWorld(cloud);}
        worldObj.playAuxSFX(2002,new net.minecraft.util.BlockPos(posX,posY,posZ),stack==null?0:stack.getItemDamage());
        setDead();
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeItemStack(buffer, stack);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		stack = ByteBufUtils.readItemStack(buffer);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("Potion"));
		if (stack == null)
			setDead();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (stack != null)
			nbt.setTag("Potion", stack.writeToNBT(new NBTTagCompound()));
	}
}
