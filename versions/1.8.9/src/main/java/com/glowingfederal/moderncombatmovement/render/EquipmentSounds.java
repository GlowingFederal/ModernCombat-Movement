/* Adapted from UAB ArmorSoundEventHandler; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.render;
import net.minecraft.item.*;
import net.minecraft.entity.player.EntityPlayer;
/** Native equipment snapshots supply owner and observer audio; UAB retains its own handler/settings. */
public final class EquipmentSounds {
 private final java.util.Map<EntityPlayer,Item[]> previous=new java.util.WeakHashMap<EntityPlayer,Item[]>();
 @net.minecraftforge.fml.common.eventhandler.SubscribeEvent public void equipment(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent event){
  if(!(event.entityLiving instanceof EntityPlayer)||!event.entityLiving.worldObj.isRemote)return;EntityPlayer player=(EntityPlayer)event.entityLiving;
  Item[] state=previous.get(player);boolean initialized=state!=null;if(!initialized){state=new Item[4];previous.put(player,state);}
  for(int slot=0;slot<4;slot++){ItemStack stack=player.inventory.armorInventory[slot];Item item=stack==null?null:stack.getItem();
   if(initialized&&item!=null&&item!=state[slot]){String sound=sound(stack);if(sound!=null)player.worldObj.playSoundAtEntity(player,"moderncombatmovement:"+sound,1,1);}state[slot]=item;
  }
 }
 private static String sound(ItemStack stack){
  if(!com.glowingfederal.moderncombatmovement.combat.ArmorRules.known(stack))return null;
  String id=com.glowingfederal.moderncombatmovement.combat.CombatHooks.id(stack),name=id.substring(id.indexOf(':')+1);
  if(name.equals("elytra"))return "item.armor.equip_elytra";
  for(String material:new String[]{"netherite","copper","leather","chainmail","iron","golden","diamond"})if(name.startsWith(material+"_"))return "item.armor.equip_"+(material.equals("golden")?"gold":material.equals("chainmail")?"chain":material);
  return null;
 }
}
