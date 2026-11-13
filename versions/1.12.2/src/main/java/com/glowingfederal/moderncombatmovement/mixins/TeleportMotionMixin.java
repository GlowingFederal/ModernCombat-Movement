package com.glowingfederal.moderncombatmovement.mixins;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=NetHandlerPlayServer.class,remap=false)
public abstract class TeleportMotionMixin {
 @Inject(method={"setPlayerLocation","func_147364_a"},at=@At("RETURN"))private void mcm$resetSpeed(CallbackInfo ci){EntityPlayerMP p=((NetHandlerPlayServer)(Object)this).player;((com.glowingfederal.moderncombatmovement.combat.MotionAccess)p).mcm$motion().reset(p.dimension,p.posX,p.posY,p.posZ);}
}
