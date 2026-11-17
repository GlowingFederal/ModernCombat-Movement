/* Adapted from UAB DispenserBehaviourTippedArrow; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;


import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviourAmmunition extends BehaviorDefaultDispenseItem {

	@Override
	public ItemStack dispenseStack(IBlockSource block, final ItemStack stack) {
		return new BehaviorProjectileDispense() {

			@Override
			protected IProjectile getProjectileEntity(World world, IPosition pos) {
				CrossbowBolt entity = new CrossbowBolt(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				entity.canBePickedUp = 1;
				return entity;
			}
		}.dispense(block, stack);
	}

	@Override
	protected void playDispenseSound(IBlockSource block) {
		block.getWorld().playAuxSFX(1002, block.getBlockPos(), 0);
	}
}