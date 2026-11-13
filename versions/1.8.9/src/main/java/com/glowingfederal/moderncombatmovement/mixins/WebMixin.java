package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.compat.KitEffects;
@Mixin(value=BlockWeb.class,remap=false)
public abstract class WebMixin {
 @Inject(method={"onEntityCollidedWithBlock","func_180634_a","func_149670_a"},at=@At("HEAD")) private void mcm$web(World world,net.minecraft.util.BlockPos pos,net.minecraft.block.state.IBlockState state,Entity entity,CallbackInfo ci) {
  if(KitEffects.has(entity,0)) entity.getEntityData().setBoolean("MCMWeavingWeb",true);
 }
}
