package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.offhand.Offhands;
/** Vanilla has one eating bit and otherwise infers main-hand use. Equipment snapshots supply the missing hand. */
@Mixin(value=EntityOtherPlayerMP.class,remap=false)
public abstract class OffhandObserverMixin {
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityOtherPlayerMP;setItemInUse(Lnet/minecraft/item/ItemStack;I)V"),require=0)private void mcm$startMcp(EntityOtherPlayerMP p,ItemStack stack,int duration){if(!Offhands.using(p))p.setItemInUse(stack,duration);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityOtherPlayerMP;func_71008_a(Lnet/minecraft/item/ItemStack;I)V"),require=0)private void mcm$startSrg(EntityOtherPlayerMP p,ItemStack stack,int duration){if(!Offhands.using(p))p.setItemInUse(stack,duration);}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityOtherPlayerMP;clearItemInUse()V"),require=0)private void mcm$stopMcp(EntityOtherPlayerMP p){if(!Offhands.using(p))p.clearItemInUse();}
 @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityOtherPlayerMP;func_71041_bz()V"),require=0)private void mcm$stopSrg(EntityOtherPlayerMP p){if(!Offhands.using(p))p.clearItemInUse();}
}
