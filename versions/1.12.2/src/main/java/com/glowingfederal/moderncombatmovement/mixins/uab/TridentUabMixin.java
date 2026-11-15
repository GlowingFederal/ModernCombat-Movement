package com.glowingfederal.moderncombatmovement.mixins.uab;
import net.minecraft.item.ItemStack;import net.minecraft.world.World;import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.Tridents;import com.glowingfederal.moderncombatmovement.config.Settings;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.TridentItem",remap=false)
public abstract class TridentUabMixin {
@Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)private void mcm$use(World world,EntityPlayer p,net.minecraft.util.EnumHand hand,CallbackInfoReturnable<net.minecraft.util.ActionResult<ItemStack>> ci){if(Settings.projectiles(world.isRemote))ci.setReturnValue(new net.minecraft.util.ActionResult<ItemStack>(Tridents.use(p.getHeldItem(hand),p,hand)?net.minecraft.util.EnumActionResult.SUCCESS:net.minecraft.util.EnumActionResult.FAIL,p.getHeldItem(hand)));}
 @Inject(method={"onPlayerStoppedUsing","func_77615_a"},at=@At("HEAD"),cancellable=true)private void mcm$release(ItemStack stack,World world,net.minecraft.entity.EntityLivingBase user,int remaining,CallbackInfo ci){if(Settings.projectiles(world.isRemote)&&user instanceof EntityPlayer){Tridents.release(stack,(EntityPlayer)user,remaining);ci.cancel();}}
}
