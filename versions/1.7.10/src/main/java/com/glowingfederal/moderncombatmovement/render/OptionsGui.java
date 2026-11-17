package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import com.glowingfederal.moderncombatmovement.config.*;
public final class OptionsGui extends cpw.mods.fml.client.config.GuiConfig {
 public OptionsGui(GuiScreen parent){super(parent,elements(),com.glowingfederal.moderncombatmovement.Identity.MOD_ID,false,false,"Modern Combat & Movement");}
 private static java.util.List<cpw.mods.fml.client.config.IConfigElement> elements(){
  java.util.List<cpw.mods.fml.client.config.IConfigElement> elements=new java.util.ArrayList<cpw.mods.fml.client.config.IConfigElement>();
  Settings.configuration.get("controls","creativeFlightMultiplier",2D).setConfigEntryClass(cpw.mods.fml.client.config.GuiConfigEntries.NumberSliderEntry.class);
  elements.add(new ConfigElement(Settings.configuration.getCategory("controls")));elements.add(new ConfigElement(Settings.configuration.getCategory("hud")));return elements;
 }
}
