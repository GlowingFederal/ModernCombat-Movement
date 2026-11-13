package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=EntityPlayerSP.class,remap=false)
public abstract class PlayerUseMovementMixin {
 @Redirect(method={"onLivingUpdate","func_70636_d"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"),require=0)private boolean mcm$mcp(EntityPlayerSP p){return p.isHandActive()&&!com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spearUsing(p);}
 @Redirect(method={"onLivingUpdate","func_70636_d"},at=@At(value="INVOKE",target="Lnet/minecraft/client/entity/EntityPlayerSP;func_184587_cr()Z"),require=0)private boolean mcm$srg(EntityPlayerSP p){return p.isHandActive()&&!com.glowingfederal.moderncombatmovement.compat.ModernEquipment.spearUsing(p);}
}
