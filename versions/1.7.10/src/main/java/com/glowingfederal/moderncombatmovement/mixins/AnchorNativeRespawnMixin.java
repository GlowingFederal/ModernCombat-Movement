/* Adapted from UAB MixinAnchorRespawn; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.glowingfederal.moderncombatmovement.compat.AnchorRespawn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
/** A valid anchor affects only this native death respawn; provider instances stay authoritative elsewhere. */
@Mixin(value=ServerConfigurationManager.class,remap=false)
public final class AnchorNativeRespawnMixin {
    @WrapMethod(method={"respawnPlayer","func_72368_a"},remap=false)
    private EntityPlayerMP mcm$respawn(EntityPlayerMP old,int dimension,boolean end,Operation<EntityPlayerMP> original){AnchorRespawn.Context selected=AnchorRespawn.begin(old,end),previous=AnchorRespawn.push(selected);try{EntityPlayerMP result=original.call(old,selected==null?dimension:selected.dimension,end);AnchorRespawn.finish(selected,result);return result;}finally{AnchorRespawn.push(previous);}}
    // Only one naming variant exists at runtime; the group requires a match, even with injection counting enabled.
    @Group(name="anchorProvider",min=1) @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"),require=0,expect=0,remap=false)
    private boolean mcm$provider(WorldProvider provider,Operation<Boolean> original){return AnchorRespawn.allow(provider,original.call(provider));}
    @Group(name="anchorProvider",min=1) @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/WorldProvider;func_76567_e()Z"),require=0,expect=0,remap=false)
    private boolean mcm$providerSrg(WorldProvider provider,Operation<Boolean> original){return AnchorRespawn.allow(provider,original.call(provider));}
    @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;getBedLocation(I)Lnet/minecraft/util/ChunkCoordinates;"),remap=false)
    private ChunkCoordinates mcm$exit(EntityPlayerMP player,int dimension,Operation<ChunkCoordinates> original){return AnchorRespawn.bed(player,dimension,original.call(player,dimension));}
    @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;isSpawnForced(I)Z"),remap=false)
    private boolean mcm$forced(EntityPlayerMP player,int dimension,Operation<Boolean> original){return AnchorRespawn.forced(player,dimension,original.call(player,dimension));}
    @Group(name="anchorStoredPoint",min=1) @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;setSpawnChunk(Lnet/minecraft/util/ChunkCoordinates;Z)V"),require=0,expect=0,remap=false)
    private void mcm$store(EntityPlayerMP player,ChunkCoordinates pos,boolean forced,Operation<Void> original){ChunkCoordinates target=AnchorRespawn.stored(player,pos);AnchorRespawn.SpawnRestore previous=AnchorRespawn.beforeRestore(player,target,forced);try{original.call(player,target,forced);}finally{AnchorRespawn.afterRestore(previous);}}
    @Group(name="anchorStoredPoint",min=1) @WrapOperation(method={"respawnPlayer","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;func_71063_a(Lnet/minecraft/util/ChunkCoordinates;Z)V"),require=0,expect=0,remap=false)
    private void mcm$storeSrg(EntityPlayerMP player,ChunkCoordinates pos,boolean forced,Operation<Void> original){ChunkCoordinates target=AnchorRespawn.stored(player,pos);AnchorRespawn.SpawnRestore previous=AnchorRespawn.beforeRestore(player,target,forced);try{original.call(player,target,forced);}finally{AnchorRespawn.afterRestore(previous);}}
}
