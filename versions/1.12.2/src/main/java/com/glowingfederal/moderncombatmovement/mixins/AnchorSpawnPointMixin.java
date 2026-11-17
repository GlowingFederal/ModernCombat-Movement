/* Adapted from UAB AnchorSpawnPointMixin; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.glowingfederal.moderncombatmovement.compat.AnchorRespawn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.injection.At;
/** Observe actual successful native spawn changes, including dimension-aware Forge setters. */
@Mixin(value=EntityPlayer.class,remap=false)
public final class AnchorSpawnPointMixin {
    @WrapMethod(method={"setSpawnPoint(Lnet/minecraft/util/math/BlockPos;Z)V","func_180473_a(Lnet/minecraft/util/math/BlockPos;Z)V"},remap=false)
    private void mcm$spawn(BlockPos pos,boolean forced,Operation<Void> original){EntityPlayer player=(EntityPlayer)(Object)this;int dimension=player.dimension;AnchorRespawn.SpawnSnapshot before=AnchorRespawn.snapshot(player,dimension);original.call(pos,forced);AnchorRespawn.changed(player,dimension,pos,forced,before);}
    @WrapMethod(method="setSpawnChunk(Lnet/minecraft/util/math/BlockPos;ZI)V",remap=false)
    private void mcm$dimension(BlockPos pos,boolean forced,int dimension,Operation<Void> original){EntityPlayer player=(EntityPlayer)(Object)this;AnchorRespawn.SpawnSnapshot before=AnchorRespawn.snapshot(player,dimension);original.call(pos,forced,dimension);AnchorRespawn.changed(player,dimension,pos,forced,before);}
    @ModifyExpressionValue(method={"setSpawnPoint","func_180473_a"},at=@At(value="INVOKE",target="Lnet/minecraftforge/event/ForgeEventFactory;onPlayerSpawnSet(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/BlockPos;Z)Z"),remap=false)
    private boolean mcm$permission(boolean canceled){AnchorRespawn.permission((EntityPlayer)(Object)this,canceled);return canceled;}
}
