package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.offhand.*;
@Mixin(value=InventoryPlayer.class,remap=false)
public abstract class OffhandInventoryMixin {
 @Unique private EntityPlayer mcm$owner(){return ((InventoryPlayer)(Object)this).player;}
 @Inject(method={"dropAllItems","func_70436_m"},at=@At("RETURN")) private void mcm$drop(CallbackInfo ci){Offhands.drop(mcm$owner());}
 @Inject(method={"clearInventory","func_146027_a"},at=@At("RETURN"),cancellable=true) private void mcm$clear(Item item,int meta,CallbackInfoReturnable<Integer> ci){ItemStack s=Offhands.get(mcm$owner());if(s!=null&&(item==null||s.getItem()==item)&&(meta<0||s.getItemDamage()==meta)){ci.setReturnValue(ci.getReturnValue()+s.stackSize);Offhands.set(mcm$owner(),null);}}
}
