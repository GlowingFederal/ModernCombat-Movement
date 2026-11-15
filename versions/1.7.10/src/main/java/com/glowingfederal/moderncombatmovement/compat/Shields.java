/* Item/use/wear integration adapted from UAB ItemShield, LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import com.glowingfederal.moderncombatmovement.combat.*;
import com.glowingfederal.moderncombatmovement.config.Settings;
import com.glowingfederal.moderncombatmovement.pose.PlayerHooks;
/** Exact item opt-in; no global held-item substitution and no second UAB defense handler. */
public final class Shields {
 private static Item item;
 private Shields(){}
 public static void register(){
  item=Content.uab?cpw.mods.fml.common.registry.GameRegistry.findItem("etfuturum","shield"):new ShieldItem();
  if(!Content.uab){cpw.mods.fml.common.registry.GameRegistry.registerItem(item,"shield");Content.ITEMS.put("shield",item);cpw.mods.fml.common.registry.GameRegistry.addRecipe(new ItemStack(item),"PIP","PPP"," P ",'P',net.minecraft.init.Blocks.planks,'I',net.minecraft.init.Items.iron_ingot);}
  if(item!=null)com.glowingfederal.moderncombatmovement.offhand.OffhandUses.register(item,new ShieldAdapter());
 }
 public static boolean known(ItemStack stack){return stack!=null&&stack.getItem()==item;}
 public static boolean enabled(EntityPlayer p){return Settings.shields(p.worldObj.isRemote)&&(!Content.uab||UabShieldAvailability.enabled());}
 public static int cooldown(EntityPlayer p){return PlayerHooks.state(p).shieldCooldown;}
 public static boolean use(ItemStack stack,EntityPlayer p){if(!enabled(p)||cooldown(p)>0)return false;p.setItemInUse(stack,72000);return true;}
 public static boolean active(EntityPlayer p){ItemStack s=((PlayerUseAccess)p).mcm$useStack();return known(s)&&enabled(p)&&cooldown(p)==0&&72000-((PlayerUseAccess)p).mcm$useRemaining()>=5;}
 public static boolean canBlock(EntityPlayer p,DamageSource source){
  if(!active(p)||source.isUnblockable()||source.isFireDamage()&&!source.isProjectile()||source.getDamageType().equals("cactus")||source.getDamageType().equals("lightningBolt")||source.getDamageType().equals("anvil"))return false;
  Entity direct=source.getSourceOfDamage();if(direct instanceof CrossbowBolt&&((CrossbowBolt)direct).piercing()>0)return false;
  if((direct==null||direct==p)&&!(source instanceof CombatBlastDamageSource))return false;
  double dx=source instanceof CombatBlastDamageSource?((CombatBlastDamageSource)source).originX()-p.posX:direct.posX-p.posX,dz=source instanceof CombatBlastDamageSource?((CombatBlastDamageSource)source).originZ()-p.posZ:direct.posZ-p.posZ,length=Math.sqrt(dx*dx+dz*dz),yaw=Math.toRadians(p.rotationYawHead);
  // BlocksAttacks: horizontal angle at most 90 degrees, including its boundary.
  return length<1E-8||(-Math.sin(yaw)*dx+Math.cos(yaw)*dz)/length>=-6.123234E-17;
 }
 public static boolean block(EntityPlayer p,DamageSource source,float damage){
  if(!(damage>0)||Float.isInfinite(damage)||!canBlock(p,source))return false;
  ItemStack shield=((PlayerUseAccess)p).mcm$useStack();
  if(damage>=3)shield.damageItem((int)Math.min(Integer.MAX_VALUE,Math.floor(1D+damage)),p);
  Entity direct=source.getSourceOfDamage();
  if(!source.isProjectile()&&direct instanceof EntityLivingBase)disable(p,(EntityLivingBase)direct);
  if(shield.stackSize<=0){if(com.glowingfederal.moderncombatmovement.offhand.Offhands.using(p))com.glowingfederal.moderncombatmovement.offhand.Offhands.set(p,null);else p.destroyCurrentEquippedItem();p.clearItemInUse();}
  p.worldObj.playSoundAtEntity(p,"random.anvil_land",.3F,.8F+p.getRNG().nextFloat()*.4F);return true;
 }
 public static void disable(EntityPlayer p,EntityLivingBase attacker){
  // Modern weapon component: known axes always disable for five seconds; Efficiency/sprinting do not roll a chance.
  ItemStack weapon=attacker.getHeldItem();com.glowingfederal.moderncombatmovement.api.ItemProperties profile=weapon==null?null:com.glowingfederal.moderncombatmovement.api.CombatApi.find(CombatHooks.id(weapon));
  if(profile!=null&&profile.kind==com.glowingfederal.moderncombatmovement.api.ItemProperties.Kind.AXE){PlayerHooks.state(p).shieldCooldown=100;p.clearItemInUse();}
 }
 private static final class ShieldAdapter implements com.glowingfederal.moderncombatmovement.offhand.OffhandUses.Adapter {
  public ItemStack use(ItemStack stack,net.minecraft.world.World world,EntityPlayer p){Shields.use(stack,p);return stack;}
  public boolean useOnBlock(ItemStack stack,EntityPlayer p,int x,int y,int z,int side,float hx,float hy,float hz){return false;}
 }
}
