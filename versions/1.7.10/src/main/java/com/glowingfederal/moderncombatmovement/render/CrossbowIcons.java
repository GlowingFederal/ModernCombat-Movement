/* UAB crossbow icon registration with native charge thresholds and actual active-hand state. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import com.glowingfederal.moderncombatmovement.compat.Crossbows;
import com.glowingfederal.moderncombatmovement.combat.PlayerUseAccess;
public final class CrossbowIcons {
 private static final IIcon[] ICONS=new IIcon[6];
 @SubscribeEvent public void stitch(TextureStitchEvent.Pre e){if(e.map.getTextureType()!=1)return;String[] names={"crossbow_standby","crossbow_pulling_0","crossbow_pulling_1","crossbow_pulling_2","crossbow_arrow","crossbow_firework"};for(int i=0;i<names.length;i++)ICONS[i]=e.map.registerIcon("moderncombatmovement:"+names[i]);}
 public static IIcon icon(ItemStack stack,EntityPlayer player){
  int index=0;
  if(Crossbows.charged(stack))index=Crossbows.firework(stack)?5:4;
  else if(player!=null&&((PlayerUseAccess)player).mcm$useStack()==stack){float progress=(float)(Crossbows.duration(stack)-((PlayerUseAccess)player).mcm$useRemaining())/Crossbows.chargeTime(stack);index=progress>=1?3:progress>=.58F?2:1;}
  return ICONS[index];
 }
}
