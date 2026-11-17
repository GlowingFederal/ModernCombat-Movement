package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.world.World;
import net.minecraft.stats.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
/** Native pearl use with the modern one-second component and creative consumption rule. */
@Mixin(value=ItemEnderPearl.class,remap=false)
public abstract class PearlUseMixin {
 @Inject(method={"onItemRightClick","func_77659_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$pearl(ItemStack stack,World world,EntityPlayer player,CallbackInfoReturnable<ItemStack> ci){
  if((Object)this!=net.minecraft.init.Items.ender_pearl||!com.glowingfederal.moderncombatmovement.config.Settings.projectiles(world.isRemote))return;
  if(PlayerHooks.state(player).pearlCooldown>0){ci.setReturnValue(stack);return;}
  if(!world.isRemote&&!world.spawnEntityInWorld(new EntityEnderPearl(world,player))){ci.setReturnValue(stack);return;}
  PlayerHooks.state(player).pearlCooldown=20;
  world.playSoundAtEntity(player,"random.bow",.5F,.4F/(player.getRNG().nextFloat()*.4F+.8F));
  if(!player.capabilities.isCreativeMode)--stack.stackSize;
  player.addStat(StatList.objectUseStats[Item.getIdFromItem(stack.getItem())],1);ci.setReturnValue(stack);
 }
}
