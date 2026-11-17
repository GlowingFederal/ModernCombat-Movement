package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.ToolInteractions;
@Mixin(value=ItemHoe.class,remap=false)
public abstract class ToolInteractionMixin {
 @Inject(method={"onItemUse","func_77648_a","func_180614_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$shieldPriority(ItemStack stack,EntityPlayer p,World world,BlockPos pos,EnumFacing face,float x,float y,float z,CallbackInfoReturnable<Boolean> ci){if(ToolInteractions.yields(p,stack))ci.setReturnValue(false);}
}
