package com.glowingfederal.moderncombatmovement.bootstrap;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
@IFMLLoadingPlugin.Name("ModernCombatMovement")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"com.glowingfederal.moderncombatmovement.bootstrap"})
public final class Bootstrap implements IFMLLoadingPlugin, IEarlyMixinLoader {
 public String getMixinConfig() { return "mixins.moderncombatmovement.json"; }
 public List<String> getMixins(Set<String> loadedCoreMods) {
  List<String> mixins=new ArrayList<String>(Arrays.asList("FireworkUseMixin","FlightPacketMixin","UabFlightBridgeMixin","FlightMixin","FlightTravelMixin","PlayerMixin","LivingMixin","CombatMixin","WebMixin","WebMovementMixin","SlimeMixin","CrossbowArrowMixin","EquipmentEnchantMixin","ForgeArmorMixin","LivingArmorMixin","CrystalMixin","TeleportMotionMixin","GoldenAppleMixin","ProtectionMixin","EyeQueryMixin"));
  if(cpw.mods.fml.relauncher.FMLLaunchHandler.side().isClient()) mixins.addAll(Arrays.asList("InputMixin","CameraMixin","RenderPlayerMixin","ModelMixin","ElytraRenderMixin","ModernHandMixin","SpearInputMixin","PlayerUseMovementMixin","PotionCreativeMixin","CrystalRenderMixin","PlayerEyeClientMixin"));
  return mixins;
 }
 public String[] getASMTransformerClass() { return new String[0]; }
 public String getModContainerClass() { return null; }
 public String getSetupClass() { return null; }
 public void injectData(Map<String,Object> data) {}
 public String getAccessTransformerClass() { return null; }
}
