package com.glowingfederal.moderncombatmovement.compat;
import java.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
/** Old UAB entity IDs are read normally and transferred at the end of their first server tick. */
public final class UabTridentBridge {
 private static final Set<Entity> pending=Collections.newSetFromMap(new IdentityHashMap<Entity,Boolean>());
 private static final Set<Entity> rejected=Collections.newSetFromMap(new WeakHashMap<Entity,Boolean>());
 public static boolean queue(Entity old){if(old.worldObj.isRemote||old.isDead||rejected.contains(old)||!com.glowingfederal.moderncombatmovement.config.Settings.projectiles(false))return false;pending.add(old);return true;}
 @SubscribeEvent public void tick(TickEvent.WorldTickEvent e){
  if(e.phase!=TickEvent.Phase.END||e.world.isRemote)return;
  for(Iterator<Entity> iterator=pending.iterator();iterator.hasNext();){
   Entity old=iterator.next();if(old.worldObj!=e.world)continue;iterator.remove();if(old.isDead)continue;
   NBTTagCompound tag=new NBTTagCompound();old.writeToNBT(tag);TridentProjectile replacement=new TridentProjectile(e.world);replacement.readFromNBT(tag);
   // Removal is immediate and outside entity iteration, so UUID indexes cannot subsequently erase the replacement.
   e.world.removePlayerEntityDangerously(old);
   if(!e.world.spawnEntityInWorld(replacement)){old.isDead=false;rejected.add(old);e.world.spawnEntityInWorld(old);}
  }
 }
 @SubscribeEvent public void unload(net.minecraftforge.event.world.WorldEvent.Unload e){for(Iterator<Entity> it=pending.iterator();it.hasNext();)if(it.next().worldObj==e.world)it.remove();}
}
