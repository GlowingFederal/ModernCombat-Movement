/* Adapted from UAB MixinAnchorSpawnPoint; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.glowingfederal.moderncombatmovement.compat.AnchorRespawn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import org.spongepowered.asm.mixin.Mixin;
/** Observe actual successful native spawn changes, including dimension-aware Forge setters. */
@Mixin(value=EntityPlayer.class,remap=false)
public final class AnchorSpawnPointMixin {
    @WrapMethod(method={"setSpawnChunk(Lnet/minecraft/util/ChunkCoordinates;Z)V","func_71063_a(Lnet/minecraft/util/ChunkCoordinates;Z)V"},remap=false)
    private void mcm$spawn(ChunkCoordinates pos,boolean forced,Operation<Void> original){EntityPlayer player=(EntityPlayer)(Object)this;int dimension=player.dimension;AnchorRespawn.SpawnSnapshot before=AnchorRespawn.snapshot(player,dimension);original.call(pos,forced);AnchorRespawn.changed(player,dimension,pos,forced,before);}
    @WrapMethod(method="setSpawnChunk(Lnet/minecraft/util/ChunkCoordinates;ZI)V",remap=false)
    private void mcm$dimension(ChunkCoordinates pos,boolean forced,int dimension,Operation<Void> original){EntityPlayer player=(EntityPlayer)(Object)this;AnchorRespawn.SpawnSnapshot before=AnchorRespawn.snapshot(player,dimension);original.call(pos,forced,dimension);AnchorRespawn.changed(player,dimension,pos,forced,before);}
}
