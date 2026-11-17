package com.glowingfederal.moderncombatmovement.bootstrap;

import java.util.List;
import java.util.Set;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Resource detection avoids loading optional APIs or game classes during bootstrap. */
public final class MixinSelection implements IMixinConfigPlugin {
    public void onLoad(String mixinPackage) {}
    public String getRefMapperConfig() { return null; }
    public boolean shouldApplyMixin(String target,String mixin) {boolean mcmDevelopment=Boolean.TRUE.equals(net.minecraft.launchwrapper.Launch.blackboard.get("fml.deobfuscatedEnvironment"));if(mixin.endsWith(".FlightSpeedDevMixin"))return mcmDevelopment;if(mixin.endsWith(".FlightSpeedMixin"))return !mcmDevelopment;
        boolean development=Boolean.TRUE.equals(net.minecraft.launchwrapper.Launch.blackboard.get("fml.deobfuscatedEnvironment"));
        if(mixin.endsWith(".ArrowGroundMixin"))return development;
        if(mixin.endsWith(".ArrowGroundSrgMixin"))return !development;
        if((mixin.endsWith(".FireproofItemMixin")||mixin.endsWith(".AnchorNativeRespawnMixin")||mixin.endsWith(".AnchorSpawnPointMixin"))&&net.minecraft.launchwrapper.Launch.classLoader.getResource("ganymedes01/etfuturum/RespawnAnchors.class")!=null)return false;
        return !mixin.endsWith(".FoodRegenMixin") || net.minecraft.launchwrapper.Launch.classLoader.getResource(
                "squeek/applecore/api/hunger/HealthRegenEvent$AllowRegen.class")==null;
    }
    public void acceptTargets(Set<String> mine,Set<String> others) {}
    public List<String> getMixins() { return null; }
    public void preApply(String target,ClassNode node,String mixin,IMixinInfo info) {}
    public void postApply(String target,ClassNode node,String mixin,IMixinInfo info) {}
}
