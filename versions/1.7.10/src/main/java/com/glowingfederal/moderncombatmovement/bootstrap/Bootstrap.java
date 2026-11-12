package com.glowingfederal.moderncombatmovement.bootstrap;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
@IFMLLoadingPlugin.Name("ModernCombatMovement")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"com.glowingfederal.moderncombatmovement.bootstrap"})
public final class Bootstrap implements IFMLLoadingPlugin {
 public Bootstrap() { MixinBootstrap.init(); Mixins.addConfiguration("mixins.moderncombatmovement.json"); }
 public String[] getASMTransformerClass() { return new String[0]; }
 public String getModContainerClass() { return null; }
 public String getSetupClass() { return null; }
 public void injectData(Map<String,Object> data) {}
 public String getAccessTransformerClass() { return null; }
}
