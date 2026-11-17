/* Native stages adapted from UAB AnchorRespawnMixin; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldProvider;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.compat.AnchorRespawn;
@Mixin(value=net.minecraft.server.management.ServerConfigurationManager.class,remap=false,priority=2000)
public abstract class AnchorNativeRespawnMixin {
 @org.spongepowered.asm.mixin.Unique private EntityPlayerMP mcm$nativeRespawn;
 /** Mixin 0.7 has no WrapMethod. Re-enter only the native body inside the same bounded UAB context. */
 @Inject(method={"recreatePlayerEntity","func_72368_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$respawn(EntityPlayerMP old,int dimension,boolean end,CallbackInfoReturnable<EntityPlayerMP> ci){
  if(mcm$nativeRespawn==old)return;
  AnchorRespawn.Context selected=AnchorRespawn.begin(old,end);
  if(selected==null&&AnchorRespawn.current()==null)return;
  AnchorRespawn.Context previous=AnchorRespawn.push(selected);
  EntityPlayerMP previousOwner=mcm$nativeRespawn;mcm$nativeRespawn=old;
  try{EntityPlayerMP result=((net.minecraft.server.management.ServerConfigurationManager)(Object)this).recreatePlayerEntity(old,selected==null?dimension:selected.dimension,end);AnchorRespawn.finish(selected,result);ci.setReturnValue(result);}
  finally{mcm$nativeRespawn=previousOwner;AnchorRespawn.push(previous);}
 }
 @Group(name="mcmAnchorProvider",min=1) @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"),require=0)
 private boolean mcm$provider(WorldProvider provider){return AnchorRespawn.allow(provider,provider.canRespawnHere());}
 @Group(name="mcmAnchorProvider",min=1) @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/WorldProvider;func_76567_e()Z"),require=0)
 private boolean mcm$providerSrg(WorldProvider provider){return AnchorRespawn.allow(provider,provider.canRespawnHere());}
 @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;getBedLocation(I)Lnet/minecraft/util/BlockPos;"))
 private BlockPos mcm$bed(EntityPlayerMP player,int dimension){return AnchorRespawn.bed(player,dimension,player.getBedLocation(dimension));}
 @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;isSpawnForced(I)Z"))
 private boolean mcm$forced(EntityPlayerMP player,int dimension){return AnchorRespawn.forced(player,dimension,player.isSpawnForced(dimension));}
 @Group(name="mcmAnchorStore",min=1) @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;setSpawnPoint(Lnet/minecraft/util/BlockPos;Z)V"),require=0)
 private void mcm$store(EntityPlayerMP player,BlockPos pos,boolean forced){mcm$restore(player,pos,forced);}
 @Group(name="mcmAnchorStore",min=1) @Redirect(method={"recreatePlayerEntity","func_72368_a"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayerMP;func_180473_a(Lnet/minecraft/util/BlockPos;Z)V"),require=0)
 private void mcm$storeSrg(EntityPlayerMP player,BlockPos pos,boolean forced){mcm$restore(player,pos,forced);}
 private void mcm$restore(EntityPlayerMP player,BlockPos pos,boolean forced){BlockPos target=AnchorRespawn.stored(player,pos);AnchorRespawn.SpawnRestore previous=AnchorRespawn.beforeRestore(player,target,forced);try{player.setSpawnPoint(target,forced);}finally{AnchorRespawn.afterRestore(previous);}}
}
