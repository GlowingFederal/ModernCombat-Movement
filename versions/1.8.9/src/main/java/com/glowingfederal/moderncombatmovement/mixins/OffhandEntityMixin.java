package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.offhand.OffhandEntities;
@Mixin(value={EntityAnimal.class,EntityCow.class,EntityMooshroom.class,EntityWolf.class,EntityOcelot.class,EntityHorse.class,EntityPig.class},remap=false)
public abstract class OffhandEntityMixin {
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;getCurrentItem()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$read(InventoryPlayer inv){return OffhandEntities.stack(inv);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;func_70448_g()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$readSrg(InventoryPlayer inv){return OffhandEntities.stack(inv);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;getHeldItem()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$held(EntityPlayer p){return OffhandEntities.held(p);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/EntityPlayer;func_70694_bm()Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$heldSrg(EntityPlayer p){return OffhandEntities.held(p);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;setInventorySlotContents(ILnet/minecraft/item/ItemStack;)V"),require=0)private void mcm$write(InventoryPlayer inv,int slot,ItemStack stack){OffhandEntities.set(inv,slot,stack);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;func_70299_a(ILnet/minecraft/item/ItemStack;)V"),require=0)private void mcm$writeSrg(InventoryPlayer inv,int slot,ItemStack stack){OffhandEntities.set(inv,slot,stack);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;decrStackSize(II)Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$take(InventoryPlayer inv,int slot,int count){return OffhandEntities.take(inv,slot,count);}
 @Redirect(method={"interact","func_70085_c"},at=@At(value="INVOKE",target="Lnet/minecraft/entity/player/InventoryPlayer;func_70298_a(II)Lnet/minecraft/item/ItemStack;"),require=0)private ItemStack mcm$takeSrg(InventoryPlayer inv,int slot,int count){return OffhandEntities.take(inv,slot,count);}
}
