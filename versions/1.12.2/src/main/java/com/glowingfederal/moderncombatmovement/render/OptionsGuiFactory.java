package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
public final class OptionsGuiFactory implements IModGuiFactory {
 public void initialize(Minecraft minecraft){}
 public boolean hasConfigGui(){return true;}public GuiScreen createConfigGui(GuiScreen parent){return new OptionsGui(parent);}
 public java.util.Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){return null;}
}
