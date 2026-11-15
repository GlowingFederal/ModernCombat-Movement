package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
public final class WindCharges {
 private static Item item;
 private WindCharges(){}
 public static void register(){
  item=Content.uab?net.minecraftforge.fml.common.registry.ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("etfuturum:wind_charge")):new WindChargeItem();if(!Content.uab){item.setRegistryName("moderncombatmovement","wind_charge");net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(Item.class).register(item);Content.ITEMS.put("wind_charge",item);}
  net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity(new net.minecraft.util.ResourceLocation("moderncombatmovement:wind_charge"),WindChargeProjectile.class,"wind_charge",5,com.glowingfederal.moderncombatmovement.ModernCombatMovement.instance,64,1,true);
  net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new Events());
 }
 public static boolean use(ItemStack stack,EntityPlayer p){
  if(!Settings.projectiles(p.world.isRemote)||Content.uab&&!UabContent.enabled("wind_charges")||p.getCooldownTracker().hasCooldown(stack.getItem()))return false;
  if(p.world.isRemote)return true;
  WindChargeProjectile projectile=new WindChargeProjectile(p.world,p);projectile.launch(p);
  if(!p.world.spawnEntity(projectile))return false;
  p.getCooldownTracker().setCooldown(stack.getItem(),10); if(!p.capabilities.isCreativeMode)stack.shrink(1);
  p.world.playSound(null,p.posX,p.posY,p.posZ,net.minecraft.init.SoundEvents.ENTITY_SNOWBALL_THROW,net.minecraft.util.SoundCategory.PLAYERS,.5F,.4F/(p.getRNG().nextFloat()*.4F+.8F)); return true;
 }
 public static void tick(EntityPlayer p){if(PlayerHooks.state(p).windCooldown>0)PlayerHooks.state(p).windCooldown--;if((p.onGround&&p.motionY<=0)||p.isInWater()||p.isOnLadder()||p.capabilities.isFlying)clear(p);}
 private static void clear(EntityLivingBase e){e.getEntityData().removeTag("MCMWindLaunchY");}
 public static void launched(EntityLivingBase e){e.fallDistance=0;e.getEntityData().setDouble("MCMWindLaunchY",e.getEntityBoundingBox().minY);}
 public static final class Events {
  @net.minecraftforge.fml.common.eventhandler.SubscribeEvent(priority=net.minecraftforge.fml.common.eventhandler.EventPriority.HIGH)public void fall(net.minecraftforge.event.entity.living.LivingFallEvent event){EntityLivingBase e=event.getEntityLiving();if(e.getEntityData().hasKey("MCMWindLaunchY")){float distance=Math.max(0,(float)(e.getEntityData().getDouble("MCMWindLaunchY")-e.getEntityBoundingBox().minY));event.setDistance(Math.min(event.getDistance(),distance)); clear(e);}}
 }
}
