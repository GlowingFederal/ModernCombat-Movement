/* Adapted from UAB RespawnedDragon; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;

public class RespawnedDragon extends EntityDragon {

	public RespawnedDragon(World world) {
		super(world);
	}

	@Override
	protected void onDeathUpdate() {
		deathTicks++;

		if (deathTicks >= 180 && deathTicks <= 200) {
			float f = (rand.nextFloat() - 0.5F) * 8.0F;
			float f1 = (rand.nextFloat() - 0.5F) * 4.0F;
			float f2 = (rand.nextFloat() - 0.5F) * 8.0F;
			worldObj.spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_HUGE, posX + f, posY + 2.0D + f1, posZ + f2, 0.0D, 0.0D, 0.0D);
		}

		int i;
		int j;

		if (!worldObj.isRemote) {
			if (deathTicks > 150 && deathTicks % 5 == 0 && worldObj.getGameRules().getBoolean("doMobLoot")) {
				i = 40;

				while (i > 0) {
					j = EntityXPOrb.getXPSplit(i);
					i -= j;
					worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, j));
				}
			}

			if (deathTicks == 1)
				worldObj.playBroadcastSound(1018, new net.minecraft.util.BlockPos(posX,posY,posZ), 0);
		}

		moveEntity(0.0D, 0.10000000149011612D, 0.0D);
		renderYawOffset = rotationYaw += 20.0F;

		if (deathTicks == 200 && !worldObj.isRemote) {
			i = worldObj.getGameRules().getBoolean("doMobLoot")?100:0;

			while (i > 0) {
				j = EntityXPOrb.getXPSplit(i);
				i -= j;
				worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj, posX, posY, posZ, j));
			}

			
			CrystalRespawn.dragonDefeated(this);
			setDead();
		}
	}
}
