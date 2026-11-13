package com.glowingfederal.moderncombatmovement.mixins;
import java.util.ArrayDeque;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.glowingfederal.moderncombatmovement.combat.ArmorRules;
/** Keep Forge's native wear and event pipeline, then apply the missing modern armor-effectiveness rule. */
@Mixin(value=net.minecraftforge.common.ISpecialArmor.ArmorProperties.class,remap=false)
public abstract class ForgeArmorMixin {
 @Unique private static final ThreadLocal<ArrayDeque<Float>> mcm$armorResults=new ThreadLocal<ArrayDeque<Float>>();
 @Inject(method="applyArmor",at=@At("HEAD"))private static void mcm$before(EntityLivingBase entity,NonNullList<ItemStack> inventory,DamageSource source,double incoming,CallbackInfoReturnable<Float> ci){
  float candidate=Float.NaN;if(entity instanceof EntityPlayer&&inventory==((EntityPlayer)entity).inventory.armorInventory)candidate=ArmorRules.reduce(entity,source,(float)incoming);ArrayDeque<Float> stack=mcm$armorResults.get();if(stack==null){stack=new ArrayDeque<Float>();mcm$armorResults.set(stack);}stack.push(candidate);
 }
 @Inject(method="applyArmor",at=@At("RETURN"),cancellable=true)private static void mcm$after(EntityLivingBase entity,NonNullList<ItemStack> inventory,DamageSource source,double incoming,CallbackInfoReturnable<Float> ci){ArrayDeque<Float> stack=mcm$armorResults.get();float value=stack.pop();if(stack.isEmpty())mcm$armorResults.remove();if(!Float.isNaN(value))ci.setReturnValue(value);}
}
