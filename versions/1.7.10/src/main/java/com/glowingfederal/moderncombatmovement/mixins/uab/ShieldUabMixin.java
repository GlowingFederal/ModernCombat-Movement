package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.Shields;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.items.ItemShield",remap=false)
public abstract class ShieldUabMixin {
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$use(ItemStack stack,World world,EntityPlayer player,CallbackInfoReturnable<ItemStack> ci){if(Settings.shields(world.isRemote)){Shields.use(stack,player);ci.setReturnValue(stack);}}
 @Inject(method="onUsingTick",at=@At("HEAD"),cancellable=true)private void mcm$tick(ItemStack stack,EntityPlayer player,int remaining,CallbackInfo ci){if(Settings.shields(player.worldObj.isRemote)){if(!Shields.enabled(player)||Shields.cooldown(player)>0)player.clearItemInUse();ci.cancel();}}
 @Inject(method="block",at=@At("HEAD"),cancellable=true)private void mcm$owner(net.minecraftforge.event.entity.living.LivingAttackEvent event,CallbackInfo ci){if(Settings.shields(event.entityLiving.worldObj.isRemote))ci.cancel();}
}
