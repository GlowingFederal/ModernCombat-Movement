package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.config.ClientOptions;
import com.glowingfederal.moderncombatmovement.movement.ToggleInput;
/** One physical-to-logical input owner; native/Combatives movement consumes the resulting keys. */
public final class ClientControls {
 private static final ToggleInput SPRINT=new ToggleInput(),SNEAK=new ToggleInput();
 private static EntityPlayer owner;
 private static int dimension;
 private static boolean controlling;
 private static float sentBoost=1;
 public static boolean external(){return net.minecraftforge.fml.common.Loader.isModLoaded("bettersprinting")||net.minecraftforge.fml.common.Loader.isModLoaded("togglesneak")||net.minecraftforge.fml.common.Loader.isModLoaded("togglesprint");}
 private ClientControls(){}
 private static boolean down(KeyBinding binding){int code=binding.getKeyCode();return code>0?org.lwjgl.input.Keyboard.isKeyDown(code):code<0&&code+100>=0&&code+100<org.lwjgl.input.Mouse.getButtonCount()&&org.lwjgl.input.Mouse.isButtonDown(code+100);}
 public static boolean valid(){Minecraft mc=Minecraft.getMinecraft();return mc.player!=null&&mc.player.isEntityAlive()&&!mc.player.isPlayerSleeping()&&mc.currentScreen==null&&mc.inGameHasFocus&&org.lwjgl.opengl.Display.isActive();}
 public static void poll(){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer player=mc.player;
  if(external()){if(controlling)release(mc);return;}
  boolean sprint=down(mc.gameSettings.keyBindSprint),sneak=down(mc.gameSettings.keyBindSneak);
  if(player!=owner||player!=null&&dimension!=player.dimension){SPRINT.reset(sprint);SNEAK.reset(sneak);sentBoost=Float.NaN;owner=player;dimension=player==null?0:player.dimension;}
  boolean valid=valid();controlling=true;
  KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(),SPRINT.update(sprint,ClientOptions.toggleSprint,valid));
  KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(),SNEAK.update(sneak,ClientOptions.toggleSneak,valid));
 }
 private static void release(Minecraft mc){SPRINT.reset(down(mc.gameSettings.keyBindSprint));SNEAK.reset(down(mc.gameSettings.keyBindSneak));KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(),false);KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(),false);controlling=false;}
 public static void reset(){release(Minecraft.getMinecraft());sentBoost=1;owner=null;}
 public static void tick(){
  Minecraft mc=Minecraft.getMinecraft();EntityPlayer player=mc.player;
  if(!valid()&&!external())release(mc);
  float requested=1;
  if(!external()&&valid()&&ClientOptions.flightBoost&&player.capabilities.isCreativeMode&&player.capabilities.isFlying&&mc.gameSettings.keyBindSprint.isKeyDown())requested=(float)Math.min(ClientOptions.flightMultiplier,ClientOptions.serverFlightLimit);
  if(player!=null&&player.world!=null&&(requested!=sentBoost||player.ticksExisted%20==0)){com.glowingfederal.moderncombatmovement.network.Packets.channel.sendToServer(new com.glowingfederal.moderncombatmovement.network.Packets.CreativeBoost(player,requested));sentBoost=requested;}
  if(player==null){sentBoost=1;owner=null;}
 }
}
