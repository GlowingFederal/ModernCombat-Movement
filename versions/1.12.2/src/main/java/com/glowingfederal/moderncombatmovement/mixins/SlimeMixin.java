package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.entity.monster.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import com.glowingfederal.moderncombatmovement.compat.SlimeAccess;
@Mixin(value=EntitySlime.class,remap=false)
public abstract class SlimeMixin implements SlimeAccess {
 @Shadow(aliases={"func_70799_a"}) protected abstract void setSlimeSize(int size,boolean resetHealth);
 public void mcm$size(int size) { setSlimeSize(size,true); }
}
