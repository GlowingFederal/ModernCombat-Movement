package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;import net.minecraft.world.World;import net.minecraft.entity.player.EntityPlayer;import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.*;import com.glowingfederal.moderncombatmovement.compat.WindCharges;import com.glowingfederal.moderncombatmovement.config.Settings;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemWindCharge",remap=false)public abstract class WindChargeUabMixin {
@Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$use(ItemStack stack,World w,EntityPlayer p,CallbackInfoReturnable<ItemStack> ci){if(Settings.projectiles(w.isRemote)){WindCharges.use(stack,p);ci.setReturnValue(stack);}}
}
