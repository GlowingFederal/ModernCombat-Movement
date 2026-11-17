package com.glowingfederal.moderncombatmovement.bootstrap;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import java.util.*;
@LateMixin
public final class UabLateMixins implements ILateMixinLoader {
 public String getMixinConfig() { return "mixins.moderncombatmovement.uab.json"; }
 public List<String> getMixins(Set<String> loadedMods) { java.util.List<String> result=new java.util.ArrayList<String>();if(!loadedMods.contains("etfuturum"))return result;result.addAll(Arrays.asList("PearlUabMixin","ProjectileEnchantUabMixin","TridentEntityUabMixin","ElytraRepairUabMixin","CrystalUabMixin","DragonUabMixin","TippedRecipeUabMixin","TippedArrowUabMixin","BowUabMixin","LingeringProjectileUabMixin","AnchorUabMixin","LingeringUabMixin","MendingUabMixin","ToolUabMixin","WindChargeUabMixin","TridentUabMixin","ShieldUabMixin","CrossbowUabMixin","MaceUabMixin","SpearUabMixin","SpearLungeUabMixin","TotemUabMixin","CrystalUabItemMixin"));if(cpw.mods.fml.relauncher.FMLLaunchHandler.side().isClient())result.add("ElytraUabRenderMixin");return result; }
}
