package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(value=ItemEndCrystal.class,remap=false)
public abstract class CrystalItemMixin {
 @Inject(method={"onItemUse","func_180614_a"},at=@At("HEAD"),cancellable=true)private void mcm$place(EntityPlayer p,World w,BlockPos pos,EnumHand hand,EnumFacing side,float hx,float hy,float hz,CallbackInfoReturnable<EnumActionResult> ci){if((Object)this==net.minecraft.init.Items.END_CRYSTAL&&com.glowingfederal.moderncombatmovement.config.Settings.kit(w.isRemote))ci.setReturnValue(com.glowingfederal.moderncombatmovement.compat.Crystals.place(p.getHeldItem(hand),p,w,pos.getX(),pos.getY(),pos.getZ(),side.getIndex())?EnumActionResult.SUCCESS:EnumActionResult.FAIL);}
}
