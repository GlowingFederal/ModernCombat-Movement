package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
public final class OptionsGuiFactory implements IModGuiFactory {
 public void initialize(Minecraft minecraft){}
 public Class<? extends GuiScreen> mainConfigGuiClass(){return OptionsGui.class;}public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement category){return null;}
 public java.util.Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){return null;}
}
