package com.glowingfederal.moderncombatmovement.mixins.uab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Pseudo
@Mixin(targets="ganymedes01.etfuturum.items.ItemEndCrystal",remap=false)
public abstract class CrystalUabItemMixin {
 @Inject(method={"onItemUse","func_77648_a"},at=@At("HEAD"),cancellable=true)private void mcm$place(ItemStack s,EntityPlayer p,World w,int x,int y,int z,int side,float hx,float hy,float hz,CallbackInfoReturnable<Boolean> ci){if(com.glowingfederal.moderncombatmovement.config.Settings.kit(w.isRemote))ci.setReturnValue(com.glowingfederal.moderncombatmovement.compat.Crystals.place(s,p,w,x,y,z,side));}
}
