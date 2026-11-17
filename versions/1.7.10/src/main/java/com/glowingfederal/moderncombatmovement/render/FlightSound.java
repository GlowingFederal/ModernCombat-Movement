/* Native 1.12 ElytraSound, adapted to the legacy sound engine. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.movement.FlightHooks;
public final class FlightSound extends net.minecraft.client.audio.MovingSound {
 private final EntityPlayer player;private int time;
 public FlightSound(EntityPlayer player){super(new net.minecraft.util.ResourceLocation("moderncombatmovement:item.elytra.flying"));this.player=player;repeat=true;field_147665_h=0;volume=.1F;}
 public void update(){
  time++;
  if(player.isDead||player!=net.minecraft.client.Minecraft.getMinecraft().thePlayer||!FlightHooks.flying(player)){donePlaying=true;return;}
  xPosF=(float)player.posX;yPosF=(float)player.posY;zPosF=(float)player.posZ;
  float speed=(float)Math.sqrt(player.motionX*player.motionX+player.motionY*player.motionY+player.motionZ*player.motionZ),factor=speed/2;
  volume=speed>=.01?Math.max(0,Math.min(1,factor*factor)):0;
  if(time<20)volume=0;else if(time<40)volume*=((time-20)/20F);
  field_147663_c=volume>.8F?1+volume-.8F:1;
 }
}
