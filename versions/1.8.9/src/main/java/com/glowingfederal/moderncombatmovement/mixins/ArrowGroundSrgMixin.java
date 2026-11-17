package com.glowingfederal.moderncombatmovement.mixins;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.glowingfederal.moderncombatmovement.compat.ArrowState;
/** Exact field access also supports UAB's access-transformed public field. */
@Mixin(value=EntityArrow.class,remap=false)
public abstract class ArrowGroundSrgMixin implements ArrowState {
 @Accessor(value="field_70254_i",remap=false) public abstract boolean mcm$inGround();
}
