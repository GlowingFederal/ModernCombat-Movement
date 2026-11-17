/* UAB EntityPlacedEndCrystal lifecycle, with the native modern ritual owner. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.world.World;
public final class PlacedCrystal extends net.minecraft.entity.item.EntityEnderCrystal {
 public PlacedCrystal(World w){super(w);}
 @Override public void onUpdate(){super.onUpdate();if(!worldObj.isRemote&&ticksExisted%20==0)CrystalRespawn.placed(this);}
}
