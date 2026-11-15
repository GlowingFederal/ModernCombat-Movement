package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.combat.CombatHooks;
public final class Shields {
 private Shields(){}
 public static boolean known(ItemStack stack){return !stack.isEmpty()&&stack.getItem()==net.minecraft.init.Items.SHIELD;}
 public static boolean owns(EntityLivingBase e){return e instanceof EntityPlayer&&Settings.shields(e.world.isRemote)&&known(e.getActiveItemStack());}
 public static boolean canBlock(EntityLivingBase e,DamageSource source){
  if(!e.isActiveItemStackBlocking()||source.isUnblockable()||source.isFireDamage()&&!source.isProjectile()||source==DamageSource.CACTUS||source==DamageSource.LIGHTNING_BOLT||source==DamageSource.ANVIL)return false;
  if(source.getImmediateSource() instanceof CrossbowBolt&&((CrossbowBolt)source.getImmediateSource()).piercing()>0)return false;
  Vec3d origin=source.getDamageLocation();if(origin==null)return false;
  double dx=origin.x-e.posX,dz=origin.z-e.posZ,length=Math.sqrt(dx*dx+dz*dz),yaw=Math.toRadians(e.rotationYawHead);
  return length<1E-8||(-Math.sin(yaw)*dx+Math.cos(yaw)*dz)/length>=-6.123234E-17;
 }
 public static void blocked(EntityPlayer p,EntityLivingBase attacker){
  ItemStack weapon=attacker.getHeldItemMainhand();com.glowingfederal.moderncombatmovement.api.ItemProperties profile=com.glowingfederal.moderncombatmovement.api.CombatApi.find(CombatHooks.id(weapon));
  boolean disable=profile!=null?profile.kind==com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.AXE:weapon.getItem().canDisableShield(weapon,p.getActiveItemStack(),p,attacker);
  if(disable){p.getCooldownTracker().setCooldown(net.minecraft.init.Items.SHIELD,100);p.resetActiveHand();p.world.setEntityState(p,(byte)30);}
 }
}
