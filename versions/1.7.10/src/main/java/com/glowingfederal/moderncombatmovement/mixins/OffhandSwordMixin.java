package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.api.CombatApi;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
@Mixin(value=ItemSword.class,remap=false)
public abstract class OffhandSwordMixin {
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$modernSword(ItemStack stack,World world,EntityPlayer p,CallbackInfoReturnable<ItemStack> ci){if(Settings.combat(world.isRemote)&&CombatApi.find(CombatHooks.id(stack))!=null)ci.setReturnValue(stack);}
}
