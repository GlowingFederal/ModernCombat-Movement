/* Adapted from UAB ModEnchantments.onPlayerPickupXP; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.combat;
import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.*;
import com.glowingfederal.moderncombatmovement.compat.Content;
import com.glowingfederal.moderncombatmovement.config.Settings;
public final class RepairEnchantments {
 private RepairEnchantments(){}
 public static void init(){net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new RepairEnchantments());}
 public static boolean enabled(EntityPlayer p){
  if(p.worldObj.isRemote||!Settings.kit(false)||EquipmentEnchantments.mending==null)return false;
  return true;
 }
 @SubscribeEvent(priority=EventPriority.HIGH) public void pickup(PlayerPickupXpEvent event){
  EntityPlayer p=event.entityPlayer;
  if(!enabled(p)||event.orb.xpValue<=0)return;
  List<ItemStack> equipped=new ArrayList<ItemStack>();equipped.add(CombatHooks.held(p));equipped.add(com.glowingfederal.moderncombatmovement.offhand.Offhands.get(p));
  Collections.addAll(equipped,p.inventory.armorInventory);
  
  for(java.util.Iterator<ItemStack> it=equipped.iterator();it.hasNext();){ItemStack stack=it.next();if(stack==null||!stack.isItemDamaged()||!stack.getItem().isRepairable()||EquipmentEnchantments.level(EquipmentEnchantments.mending,stack)<=0)it.remove();}
  int xp=event.orb.xpValue;
  while(xp>0&&!equipped.isEmpty()){
   int slot=p.getRNG().nextInt(equipped.size());ItemStack stack=equipped.get(slot);
   int repair=(int)Math.min((long)xp*2,stack.getItemDamage());stack.setItemDamage(stack.getItemDamage()-repair);xp-=repair/2;
   if(!stack.isItemDamaged()||repair==0)equipped.remove(slot);
  }
  event.orb.xpValue=xp;p.inventory.markDirty();
 }
}
