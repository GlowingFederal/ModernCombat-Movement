package com.glowingfederal.moderncombatmovement.mixins;
@org.spongepowered.asm.mixin.Mixin(value=net.minecraft.entity.EntityLivingBase.class,remap=false)
public abstract class EquipmentSoundMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method={"playEquipSound","func_184606_a_"},at=@org.spongepowered.asm.mixin.injection.At("HEAD"),cancellable=true)
 private void mcm$equipment(net.minecraft.item.ItemStack stack,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){
  if(stack.isEmpty()||!com.glowingfederal.moderncombatmovement.combat.ArmorRules.known(stack))return;
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack),name=id.substring(id.indexOf(':')+1);
  if(name.startsWith("netherite_")||name.startsWith("copper_")){com.glowingfederal.moderncombatmovement.compat.ItemSounds.play((net.minecraft.entity.Entity)(Object)this,name.startsWith("netherite_")?"item.armor.equip_netherite":"item.armor.equip_copper",1,1);ci.cancel();}
 }
}
