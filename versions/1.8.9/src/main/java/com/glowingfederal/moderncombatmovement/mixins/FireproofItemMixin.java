/* Adapted from UAB ItemPhysicsMixin / uninflammableitem.MixinEntityItem; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraft.block.material.Material;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.EquipmentItems;
@Mixin(value=EntityItem.class,remap=false)
public abstract class FireproofItemMixin extends Entity {
 @Unique private boolean mcm$ownedImmunity,mcm$previousImmunity;
 public FireproofItemMixin(World world){super(world);}
 @Inject(method={"onUpdate","func_70071_h_"},at=@At("HEAD"))
 private void mcm$component(CallbackInfo ci){boolean immune=EquipmentItems.fireproof(((EntityItem)(Object)this).getEntityItem());
  if(immune&&!mcm$ownedImmunity){mcm$previousImmunity=isImmuneToFire;mcm$ownedImmunity=true;isImmuneToFire=true;}
  else if(!immune&&mcm$ownedImmunity){isImmuneToFire=mcm$previousImmunity;mcm$ownedImmunity=false;}
 }
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD"),cancellable=true)
 private void mcm$fire(DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){if(source.isFireDamage()&&EquipmentItems.fireproof(((EntityItem)(Object)this).getEntityItem()))ci.setReturnValue(false);}
 @Group(name="mcmItemLava",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/block/Block;getMaterial()Lnet/minecraft/block/material/Material;"),require=0,expect=0)
 private Material mcm$lavaMcp(net.minecraft.block.Block state){Material material=state.getMaterial();return material==Material.lava&&EquipmentItems.fireproof(((EntityItem)(Object)this).getEntityItem())?Material.air:material;}
 @Group(name="mcmItemLava",min=1) @Redirect(method={"onUpdate","func_70071_h_"},at=@At(value="INVOKE",target="Lnet/minecraft/block/Block;func_149688_o()Lnet/minecraft/block/material/Material;"),require=0,expect=0)
 private Material mcm$lavaSrg(net.minecraft.block.Block state){Material material=state.getMaterial();return material==Material.lava&&EquipmentItems.fireproof(((EntityItem)(Object)this).getEntityItem())?Material.air:material;}
}
