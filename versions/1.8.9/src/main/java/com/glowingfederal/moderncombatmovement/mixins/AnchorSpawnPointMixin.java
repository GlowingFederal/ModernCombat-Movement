/* Spawn ownership adapted from UAB AnchorSpawnPointMixin; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.AnchorRespawn;
@Mixin(value=EntityPlayer.class,remap=false)
public abstract class AnchorSpawnPointMixin {
 @Unique private java.util.Deque<AnchorRespawn.SpawnSnapshot> mcm$spawns=new java.util.ArrayDeque<AnchorRespawn.SpawnSnapshot>();
 @Inject(method={"setSpawnPoint(Lnet/minecraft/util/BlockPos;Z)V","func_180473_a(Lnet/minecraft/util/BlockPos;Z)V"},at=@At("HEAD"))private void mcm$before(BlockPos pos,boolean forced,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;mcm$spawns.push(AnchorRespawn.snapshot(p,p.dimension));}
 @Inject(method={"setSpawnPoint(Lnet/minecraft/util/BlockPos;Z)V","func_180473_a(Lnet/minecraft/util/BlockPos;Z)V"},at=@At("RETURN"))private void mcm$after(BlockPos pos,boolean forced,CallbackInfo ci){EntityPlayer p=(EntityPlayer)(Object)this;AnchorRespawn.changed(p,p.dimension,pos,forced,mcm$spawns.pop());}
 @Inject(method="setSpawnChunk(Lnet/minecraft/util/BlockPos;ZI)V",at=@At("HEAD"))private void mcm$beforeDimension(BlockPos pos,boolean forced,int dimension,CallbackInfo ci){mcm$spawns.push(AnchorRespawn.snapshot((EntityPlayer)(Object)this,dimension));}
 @Inject(method="setSpawnChunk(Lnet/minecraft/util/BlockPos;ZI)V",at=@At("RETURN"))private void mcm$afterDimension(BlockPos pos,boolean forced,int dimension,CallbackInfo ci){AnchorRespawn.changed((EntityPlayer)(Object)this,dimension,pos,forced,mcm$spawns.pop());}
 @Redirect(method={"setSpawnPoint","func_180473_a"},at=@At(value="INVOKE",target="Lnet/minecraftforge/event/ForgeEventFactory;onPlayerSpawnSet(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/BlockPos;Z)Z"))
 private boolean mcm$permission(EntityPlayer p,BlockPos pos,boolean forced){boolean canceled=net.minecraftforge.event.ForgeEventFactory.onPlayerSpawnSet(p,pos,forced);AnchorRespawn.permission(p,canceled);return canceled;}
}
