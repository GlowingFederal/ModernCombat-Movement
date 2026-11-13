package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=ItemAppleGold.class,remap=false)
public abstract class GoldenAppleMixin {
 @Inject(method={"onFoodEaten","func_77849_c"},at=@At("HEAD"),cancellable=true)private void mcm$modernApple(ItemStack stack,World world,EntityPlayer p,CallbackInfo ci){if((Object)this==net.minecraft.init.Items.golden_apple&&com.glowingfederal.moderncombatmovement.combat.CombatConsumables.apple(stack,p))ci.cancel();}
}
