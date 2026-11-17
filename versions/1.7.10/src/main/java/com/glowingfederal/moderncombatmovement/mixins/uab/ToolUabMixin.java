package com.glowingfederal.moderncombatmovement.mixins.uab;
import java.util.Set;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import com.glowingfederal.moderncombatmovement.compat.ToolInteractions;
/** Retain UAB's live tool-action filters and veto only a known main-hand transform that yields to blocking. */
@Pseudo @Mixin(targets="ganymedes01.etfuturum.core.handlers.ServerEventHandler",remap=false)
public abstract class ToolUabMixin {
 @Redirect(method="onPlayerInteract",at=@At(value="INVOKE",target="Lnet/minecraft/item/Item;getToolClasses(Lnet/minecraft/item/ItemStack;)Ljava/util/Set;"))
 private Set<String> mcm$toolPriority(Item item,ItemStack stack,PlayerInteractEvent event){return ToolInteractions.yields(event.entityPlayer,stack)?java.util.Collections.<String>emptySet():item.getToolClasses(stack);}
}
