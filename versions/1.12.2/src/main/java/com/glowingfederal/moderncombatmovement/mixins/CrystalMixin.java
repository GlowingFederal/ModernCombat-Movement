package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.*;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import com.glowingfederal.moderncombatmovement.compat.Crystals;
import com.glowingfederal.moderncombatmovement.combat.CombatBlast;
import com.glowingfederal.moderncombatmovement.config.Settings;
@Mixin(value=EntityEnderCrystal.class,remap=false)
public abstract class CrystalMixin {
 @Unique private DamageSource mcm$crystalSource;
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD"))private void mcm$source(DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){mcm$crystalSource=source;}

 @Redirect(method={"attackEntityFrom","func_70097_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"),require=0)
 private Explosion mcm$mcp(World w,Entity exploder,double x,double y,double z,float radius,boolean fire,boolean terrain){
  if(Settings.kit(w.isRemote)&&Crystals.known((Entity)(Object)this)){if(mcm$crystalSource==null||!mcm$crystalSource.isExplosion())CombatBlast.crystal(w,(Entity)(Object)this,mcm$crystalSource==null?null:mcm$crystalSource.getTrueSource(),x,y,z);return null;}
  return w.newExplosion(exploder,x,y,z,radius,fire,terrain);
 }

 @Redirect(method={"attackEntityFrom","func_70097_a"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72885_a(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"),require=0)
 private Explosion mcm$srg(World w,Entity exploder,double x,double y,double z,float radius,boolean fire,boolean terrain){
  if(Settings.kit(w.isRemote)&&Crystals.known((Entity)(Object)this)){if(mcm$crystalSource==null||!mcm$crystalSource.isExplosion())CombatBlast.crystal(w,(Entity)(Object)this,mcm$crystalSource==null?null:mcm$crystalSource.getTrueSource(),x,y,z);return null;}
  return w.newExplosion(exploder,x,y,z,radius,fire,terrain);
 }
}
