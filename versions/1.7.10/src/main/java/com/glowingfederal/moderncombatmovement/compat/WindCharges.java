package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
public final class WindCharges {
 private static Item item;
 private WindCharges(){}
 public static boolean known(ItemStack stack){return stack!=null&&stack.getItem()==item;}
 public static void register(){
  item=Content.uab?cpw.mods.fml.common.registry.GameRegistry.findItem("etfuturum","wind_charge"):new WindChargeItem();if(!Content.uab){cpw.mods.fml.common.registry.GameRegistry.registerItem(item,"wind_charge");Content.ITEMS.put("wind_charge",item);}
  cpw.mods.fml.common.registry.EntityRegistry.registerModEntity(WindChargeProjectile.class,"wind_charge",5,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,1,true);
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());if(item!=null)com.glowingfederal.moderncombatmovement.offhand.OffhandUses.register(item,new HandAdapter());
 }
 public static boolean use(ItemStack stack,EntityPlayer p){
  if(!Settings.projectiles(p.worldObj.isRemote)||Content.uab&&!UabContent.enabled("wind_charges")||PlayerHooks.state(p).windCooldown>0)return false;
  if(p.worldObj.isRemote)return true;
  WindChargeProjectile projectile=new WindChargeProjectile(p.worldObj,p);projectile.launch(p);
  if(!p.worldObj.spawnEntityInWorld(projectile))return false;
  PlayerHooks.state(p).windCooldown=10; if(!p.capabilities.isCreativeMode)--stack.stackSize;
  ItemSounds.play(p,"entity.wind_charge.throw",.5F,.4F/(p.getRNG().nextFloat()*.4F+.8F)); return true;
 }
 public static void tick(EntityPlayer p){if(PlayerHooks.state(p).windCooldown>0)PlayerHooks.state(p).windCooldown--;if((p.onGround&&p.motionY<=0)||p.isInWater()||p.isOnLadder()||p.capabilities.isFlying)clear(p);}
 private static void clear(EntityLivingBase e){e.getEntityData().removeTag("MCMWindLaunchY");}
 public static void launched(EntityLivingBase e){e.fallDistance=0;e.getEntityData().setDouble("MCMWindLaunchY",e.boundingBox.minY);}
 private static final class HandAdapter implements com.glowingfederal.moderncombatmovement.offhand.OffhandUses.Adapter {public ItemStack use(ItemStack stack,net.minecraft.world.World w,EntityPlayer p){WindCharges.use(stack,p);return stack;}public boolean useOnBlock(ItemStack stack,EntityPlayer p,int x,int y,int z,int side,float hx,float hy,float hz){return false;}}
 public static final class Events {
  @cpw.mods.fml.common.eventhandler.SubscribeEvent(priority=cpw.mods.fml.common.eventhandler.EventPriority.HIGH)public void fall(net.minecraftforge.event.entity.living.LivingFallEvent event){EntityLivingBase e=event.entityLiving;if(e.getEntityData().hasKey("MCMWindLaunchY")){float distance=Math.max(0,(float)(e.getEntityData().getDouble("MCMWindLaunchY")-e.boundingBox.minY));event.distance=Math.min(event.distance,distance); clear(e);}}
 }
}
