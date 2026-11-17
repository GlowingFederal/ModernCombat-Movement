package com.glowingfederal.moderncombatmovement.mixins;
import com.glowingfederal.moderncombatmovement.compat.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
@Mixin(value=EntityEnderCrystal.class,remap=false)
public abstract class CrystalStateMixin extends Entity implements CrystalState {
 protected CrystalStateMixin(World w){super(w);}
 @Inject(method={"entityInit","func_70088_a"},at=@At("RETURN"))private void mcm$init(CallbackInfo ci){if(Crystals.known(this)){dataWatcher.addObject(9,0);dataWatcher.addObject(10,0);dataWatcher.addObject(11,0);dataWatcher.addObject(12,(byte)0);}}
 public void mcm$beam(int x,int y,int z,boolean active){dataWatcher.updateObject(9,x);dataWatcher.updateObject(10,y);dataWatcher.updateObject(11,z);dataWatcher.updateObject(12,(byte)((dataWatcher.getWatchableObjectByte(12)&2)|(active?1:0)));}
 public boolean mcm$beamActive(){return(dataWatcher.getWatchableObjectByte(12)&1)!=0;}
 public int mcm$beamX(){return dataWatcher.getWatchableObjectInt(9);}public int mcm$beamY(){return dataWatcher.getWatchableObjectInt(10);}public int mcm$beamZ(){return dataWatcher.getWatchableObjectInt(11);}
 public void mcm$invulnerable(boolean value){dataWatcher.updateObject(12,(byte)((dataWatcher.getWatchableObjectByte(12)&1)|(value?2:0)));}
 public boolean mcm$invulnerable(){return(dataWatcher.getWatchableObjectByte(12)&2)!=0;}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("HEAD"),cancellable=true)private void mcm$protect(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){if(Crystals.known(this)&&mcm$invulnerable()&&!source.canHarmInCreative()&&!(source.getEntity() instanceof net.minecraft.entity.player.EntityPlayer&&((net.minecraft.entity.player.EntityPlayer)source.getEntity()).capabilities.isCreativeMode))ci.setReturnValue(false);}
 @Inject(method={"attackEntityFrom","func_70097_a"},at=@At("RETURN"))private void mcm$destroyed(net.minecraft.util.DamageSource source,float amount,CallbackInfoReturnable<Boolean> ci){if(isDead&&Crystals.known(this))CrystalRespawn.destroyed((EntityEnderCrystal)(Object)this);}
 @Inject(method={"writeEntityToNBT","func_70014_b"},at=@At("RETURN"))private void mcm$write(NBTTagCompound n,CallbackInfo ci){if(!Crystals.known(this))return;n.setBoolean("MCMBeam",mcm$beamActive());n.setInteger("MCMBeamX",mcm$beamX());n.setInteger("MCMBeamY",mcm$beamY());n.setInteger("MCMBeamZ",mcm$beamZ());n.setBoolean("MCMRitualInvulnerable",mcm$invulnerable());}
 @Inject(method={"readEntityFromNBT","func_70037_a"},at=@At("RETURN"))private void mcm$read(NBTTagCompound n,CallbackInfo ci){if(!Crystals.known(this))return;mcm$beam(n.getInteger("MCMBeamX"),n.getInteger("MCMBeamY"),n.getInteger("MCMBeamZ"),n.getBoolean("MCMBeam"));mcm$invulnerable(n.getBoolean("MCMRitualInvulnerable"));}
}
