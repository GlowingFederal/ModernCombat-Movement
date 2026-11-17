package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.glowingfederal.moderncombatmovement.compat.ArrowState;
/** Exact field access also supports UAB's access-transformed public field. */
@Mixin(value=EntityArrow.class,remap=false)
public abstract class ArrowGroundMixin implements ArrowState {
 @Accessor(value="inGround",remap=false) public abstract boolean mcm$inGround();
}
