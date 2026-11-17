package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
/** Version-native sound dispatch for unchanged modern item assets. */
public final class ItemSounds {
 private ItemSounds(){}
  public static void init(){}
 public static void play(Entity entity,String event,float volume,float pitch){if(!(entity instanceof net.minecraft.entity.player.EntityPlayer)&&(event.startsWith("item.trident.")||event.equals("item.crossbow.hit")))event+=".neutral";if(!entity.worldObj.isRemote){entity.worldObj.playSoundAtEntity(entity,"moderncombatmovement:"+event,volume,pitch);}}
 public static void at(World world,double x,double y,double z,String event,float volume,float pitch){if(!world.isRemote){world.playSoundEffect(x,y,z,"moderncombatmovement:"+event,volume,pitch);}}
}
