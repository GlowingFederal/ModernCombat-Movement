package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;import net.minecraft.world.World;import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.Tridents;import com.glowingfederal.moderncombatmovement.config.Settings;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemTrident",remap=false)
public abstract class TridentUabMixin {
@Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$use(ItemStack stack,World world,EntityPlayer p,CallbackInfoReturnable<ItemStack> ci){if(Settings.projectiles(world.isRemote)){Tridents.use(stack,p);ci.setReturnValue(stack);}}
 @Inject(method={"onPlayerStoppedUsing","func_77615_a"},at=@At("HEAD"),cancellable=true)private void mcm$release(ItemStack stack,World world,EntityPlayer user,int remaining,CallbackInfo ci){if(Settings.projectiles(world.isRemote)){Tridents.release(stack,user,remaining);ci.cancel();}}
}
