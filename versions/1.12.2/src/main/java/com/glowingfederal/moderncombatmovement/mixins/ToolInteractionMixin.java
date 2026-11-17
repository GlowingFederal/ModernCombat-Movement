package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.ToolInteractions;
@Mixin(value={ItemHoe.class,ItemSpade.class},remap=false)
public abstract class ToolInteractionMixin {
 @Inject(method={"onItemUse","func_77648_a","func_180614_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$shieldPriority(EntityPlayer p,World world,BlockPos pos,net.minecraft.util.EnumHand hand,EnumFacing face,float x,float y,float z,CallbackInfoReturnable<net.minecraft.util.EnumActionResult> ci){if(hand==net.minecraft.util.EnumHand.MAIN_HAND&&ToolInteractions.yields(p,p.getHeldItem(hand)))ci.setReturnValue(net.minecraft.util.EnumActionResult.PASS);}
}
