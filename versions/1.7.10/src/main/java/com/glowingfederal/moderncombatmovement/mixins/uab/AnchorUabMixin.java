package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.blocks.BlockRespawnAnchor",remap=false)
public abstract class AnchorUabMixin {
 @Inject(method={"onBlockActivated","func_149727_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$offhandCharge(World world,int x,int y,int z,EntityPlayer player,int side,float hx,float hy,float hz,CallbackInfoReturnable<Boolean> ci){
  if(com.glowingfederal.moderncombatmovement.compat.Anchors.preferOffhand(player,player.getHeldItem(),world.getBlockMetadata(x,y,z)))ci.setReturnValue(false);
 }
}
