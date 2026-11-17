package com.glowingfederal.moderncombatmovement.compat;
import java.util.UUID;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
public final class GroundRockets {
 private GroundRockets(){}
 public static boolean owns(EntityFireworkRocket rocket){return rocket.getClass()==EntityFireworkRocket.class&&com.glowingfederal.moderncombatmovement.config.Settings.projectiles(rocket.worldObj.isRemote);}
 public static void initialize(EntityFireworkRocket rocket,ItemStack stack){
  if(!owns(rocket)||stack==null)return;
  // Reuse native persistence to update its authoritative fields without shadowing access-transformed data.
  NBTTagCompound nativeState=new NBTTagCompound();rocket.writeEntityToNBT(nativeState);ItemStack copy=stack.copy();copy.stackSize=1;
  nativeState.setTag("FireworksItem",copy.writeToNBT(new NBTTagCompound()));
  if(!stack.hasTagCompound()||!stack.getTagCompound().hasKey("Fireworks",10))nativeState.setInteger("LifeTime",nativeState.getInteger("LifeTime")+10);
  rocket.readEntityFromNBT(nativeState);
 }
 public static ItemStack item(EntityFireworkRocket rocket){return rocket.getDataWatcher().getWatchableObjectItemStack(8);}
 public static void launched(Entity entity,EntityPlayer player){if(entity instanceof EntityFireworkRocket&&owns((EntityFireworkRocket)entity))entity.getEntityData().setString("MCMRocketOwner",player.getUniqueID().toString());}
 public static EntityPlayer owner(EntityFireworkRocket rocket){String value=rocket.getEntityData().getString("MCMRocketOwner");try{return value.isEmpty()?null:rocket.worldObj.getPlayerEntityByUUID(UUID.fromString(value));}catch(IllegalArgumentException invalid){return null;}}
 public static void damage(EntityFireworkRocket rocket){RocketEffects.damage(rocket,owner(rocket),null,item(rocket));}
 public static void explode(EntityFireworkRocket rocket){rocket.worldObj.setEntityState(rocket,(byte)17);damage(rocket);rocket.setDead();}
}
