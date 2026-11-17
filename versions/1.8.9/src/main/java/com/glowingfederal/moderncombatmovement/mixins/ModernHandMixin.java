package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.glowingfederal.moderncombatmovement.render.*;
import com.glowingfederal.moderncombatmovement.compat.*;
@Mixin(value=ItemRenderer.class,remap=false)
public abstract class ModernHandMixin {
 @Shadow(aliases="field_78453_b")private ItemStack itemToRender;
 @Shadow(aliases="func_178096_b")protected abstract void transformFirstPersonItem(float equip,float swing);
 @Shadow(aliases="func_178105_d")protected abstract void doItemUsedTransformations(float swing);
 @Shadow(aliases="field_78454_c")private float equippedProgress;
 @Shadow(aliases="field_78451_d")private float prevEquippedProgress;
 @Unique private float mcm$partial;
 @Inject(method={"renderItemInFirstPerson","func_78440_a"},at=@At("HEAD"),cancellable=true)private void mcm$partial(float partial,CallbackInfo ci){mcm$partial=partial;com.glowingfederal.moderncombatmovement.render.OffhandClient.firstPerson(partial);net.minecraft.entity.player.EntityPlayer p=net.minecraft.client.Minecraft.getMinecraft().thePlayer;if(p!=null&&com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)){ItemStack active=((com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess)p).mcm$useStack();if(active!=null&&(active.getItem() instanceof net.minecraft.item.ItemBow||Crossbows.known(active))){ci.cancel();return;}}if(p!=null&&(Shields.known(itemToRender)||Tridents.known(itemToRender))){HeldEquipment.firstPerson(p,itemToRender,1,1-(prevEquippedProgress+(equippedProgress-prevEquippedProgress)*partial),p.getSwingProgress(partial),p.getItemInUse()==itemToRender,partial);ci.cancel();}}
 @Unique private boolean mcm$owned(){return com.glowingfederal.moderncombatmovement.config.Settings.kit(true)&&(Crossbows.known(itemToRender)||ModernEquipment.spear(itemToRender)!=null);}

 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"),require=0)private void mcm$baseMcp(ItemRenderer renderer,float equip,float swing){mcm$transform(equip,swing);}
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/ItemRenderer;doItemUsedTransformations(F)V"),require=0)private void mcm$swingMcp(ItemRenderer renderer,float swing){if(!mcm$owned())doItemUsedTransformations(swing);}

 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/ItemRenderer;func_178096_b(FF)V"),require=0)private void mcm$baseSrg(ItemRenderer renderer,float equip,float swing){mcm$transform(equip,swing);}
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/ItemRenderer;func_178105_d(F)V"),require=0)private void mcm$swingSrg(ItemRenderer renderer,float swing){if(!mcm$owned())doItemUsedTransformations(swing);}

 @Unique private void mcm$transform(float equip,float swing){
  if(!mcm$owned()){transformFirstPersonItem(equip,swing);return;}EntityPlayer p=Minecraft.getMinecraft().thePlayer;
  if(Crossbows.known(itemToRender))CrossbowHandTransforms.apply(HandMatrices.INSTANCE,1,equip,p.getSwingProgress(mcm$partial),p.getItemInUse()==itemToRender,p.getItemInUseDuration()+mcm$partial-1,Crossbows.chargeTime(itemToRender),Crossbows.charged(itemToRender),true);
  else SpearAnimationMath.firstPerson(HandMatrices.INSTANCE,ModernEquipment.spear(itemToRender),1,equip,p.getSwingProgress(mcm$partial),p.getItemInUse()==itemToRender,p.getItemInUseDuration()+mcm$partial-1,com.glowingfederal.moderncombatmovement.pose.PlayerHooks.state(p).kineticHitTicks+mcm$partial);
 }
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;getItemInUseCount()I"),require=0)private int mcm$mainUseMcp(net.minecraft.client.entity.AbstractClientPlayer p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
 @Redirect(method={"renderItemInFirstPerson","func_78440_a"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/AbstractClientPlayer;func_71052_bv()I"),require=0)private int mcm$mainUseSrg(net.minecraft.client.entity.AbstractClientPlayer p){return com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p)?0:p.getItemInUseCount();}
}
