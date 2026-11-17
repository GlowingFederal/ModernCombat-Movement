package com.glowingfederal.moderncombatmovement.bootstrap;
import java.util.*;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.*;
/** Resource lookup keeps optional UAB classes out of the bootstrap loading path. */
public final class MixinSelection implements IMixinConfigPlugin {
 public void onLoad(String name){} public String getRefMapperConfig(){return null;}
 public boolean shouldApplyMixin(String target,String mixin){boolean mcmDevelopment=Boolean.TRUE.equals(net.minecraft.launchwrapper.Launch.blackboard.get("fml.deobfuscatedEnvironment"));if(mixin.endsWith(".FlightSpeedDevMixin"))return mcmDevelopment;if(mixin.endsWith(".FlightSpeedMixin"))return !mcmDevelopment;return !(mixin.endsWith(".FireproofItemMixin")||mixin.endsWith(".AnchorNativeRespawnMixin")||mixin.endsWith(".AnchorSpawnPointMixin"))||net.minecraft.launchwrapper.Launch.classLoader.getResource("ganymedes01/etfuturum/RespawnAnchors.class")==null;}
 public void acceptTargets(Set<String> mine,Set<String> others){} public List<String> getMixins(){return null;}
 public void preApply(String target,ClassNode node,String mixin,IMixinInfo info){} public void postApply(String target,ClassNode node,String mixin,IMixinInfo info){}
}
