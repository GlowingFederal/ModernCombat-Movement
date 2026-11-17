package com.glowingfederal.moderncombatmovement.mixins.uab;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Pseudo @Mixin(targets="ganymedes01.etfuturum.entities.EntityRespawnedDragon",remap=false)
public abstract class DragonUabMixin {
 @Inject(method={"onDeathUpdate","func_70609_aI"},at=@At("RETURN"))private void mcm$portal(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){net.minecraft.entity.boss.EntityDragon dragon=(net.minecraft.entity.boss.EntityDragon)(Object)this;if(dragon.isDead&&!dragon.worldObj.isRemote&&com.glowingfederal.moderncombatmovement.config.Settings.kit(false))com.glowingfederal.moderncombatmovement.compat.CrystalRespawn.dragonDefeated(dragon);}
 @ModifyConstant(method={"onDeathUpdate","func_70609_aI"},constant=@Constant(intValue=1000))private int mcm$xp(int original){return com.glowingfederal.moderncombatmovement.config.Settings.kit(false)?40:original;}
 @ModifyConstant(method={"onDeathUpdate","func_70609_aI"},constant=@Constant(intValue=2000))private int mcm$lastXp(int original){return com.glowingfederal.moderncombatmovement.config.Settings.kit(false)?100:original;}
 @Redirect(method={"onDeathUpdate","func_70609_aI"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$loot(net.minecraft.world.World w,net.minecraft.entity.Entity e){return (!com.glowingfederal.moderncombatmovement.config.Settings.kit(false)||w.getGameRules().getGameRuleBooleanValue("doMobLoot"))&&w.spawnEntityInWorld(e);}
 @Redirect(method={"onDeathUpdate","func_70609_aI"},at=@At(value="INVOKE",target="Lnet/minecraft/world/World;func_72838_d(Lnet/minecraft/entity/Entity;)Z"),require=0)
 private boolean mcm$lootSrg(net.minecraft.world.World w,net.minecraft.entity.Entity e){return (!com.glowingfederal.moderncombatmovement.config.Settings.kit(false)||w.getGameRules().getGameRuleBooleanValue("doMobLoot"))&&w.spawnEntityInWorld(e);}
}
