/* Adapted from UAB EntityTrident; LGPL-3.0. */
package com.glowingfederal.moderncombatmovement.compat;



import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.datasync.*;
import net.minecraft.world.World;

/** The thrown stack is authoritative and is transferred exactly once on impact. */
public class TridentProjectile extends EntityThrowable implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {
	private static final DataParameter<Byte> RETURNING = EntityDataManager.createKey(TridentProjectile.class, DataSerializers.BYTE);
	private ItemStack stack;
	private boolean returning,embedded,dealtDamage,creative;
    private int groundTicks;
    private int groundX,groundY,groundZ;
    private boolean groundKnown;
	@Override protected void entityInit() { super.entityInit(); dataManager.register(RETURNING, (byte) 0); }
	private java.util.UUID owner;
    private EntityLivingBase resolvedOwner;
	public TridentProjectile(World world) { super(world); }
	public TridentProjectile(World world, EntityLivingBase shooter, ItemStack stack) { super(world, shooter); resolvedOwner=shooter;owner = shooter.getUniqueID(); this.stack = stack == null ? null : stack.copy(); if (this.stack != null) this.stack.setCount(1); }
	@Override protected float getGravityVelocity() { return .05F; }
	@Override protected void onImpact(RayTraceResult hit) {
		if (world.isRemote) return;
		if (returning || hit.entityHit != null && dealtDamage) return;
        boolean thunder=false;
		if (hit.entityHit != null) {
			int impaling = Tridents.level(Tridents.impaling, stack);
			boolean aquatic = Tridents.aquatic(hit.entityHit);
			boolean damaged=hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 8 + (aquatic ? impaling * 2.5F : 0));
			if (damaged && world.isThundering() && Tridents.level(Tridents.channeling, stack) > 0 && world.canSeeSky(new BlockPos(hit.entityHit))) {world.addWeatherEffect(new net.minecraft.entity.effect.EntityLightningBolt(world, hit.entityHit.posX, hit.entityHit.posY, hit.entityHit.posZ, false));ItemSounds.play(this,"item.trident.thunder",5,1);thunder=true;}
		}

        if(hit.entityHit!=null){if(!thunder)ItemSounds.play(this,"item.trident.hit",1,1);dealtDamage=true;motionX*=-.02;motionY*=-.2;motionZ*=-.02;}
        else{ProjectileBlocks.hit(world,getThrower(),hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ());if(world.isThundering()&&Tridents.level(Tridents.channeling,stack)>0&&UabCombatBlocks.rod(world,hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ())&&world.canSeeSky(hit.getBlockPos().up())){world.addWeatherEffect(new net.minecraft.entity.effect.EntityLightningBolt(world,hit.getBlockPos().getX()+.5,hit.getBlockPos().getY()+1,hit.getBlockPos().getZ()+.5,false));UabCombatBlocks.strike(world,hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ());ItemSounds.play(this,"item.trident.thunder",5,1);thunder=true;}if(!thunder)ItemSounds.play(this,"item.trident.hit_ground",1,1);rememberGround(hit.getBlockPos().getX(),hit.getBlockPos().getY(),hit.getBlockPos().getZ());embedded=true;setPosition(hit.hitVec.x,hit.hitVec.y,hit.hitVec.z);motionX=motionY=motionZ=0;syncFlags();}
        if(dealtDamage&&Tridents.level(Tridents.loyalty,stack)>0){returning=true;ItemSounds.play(this,"item.trident.return",10,1);syncFlags(); noClip=true;}
    }
    public void creative(boolean value){creative=value;}
    @Override public void onCollideWithPlayer(EntityPlayer player){
        if(world.isRemote||!embedded&&!returning||owner!=null&&!owner.equals(player.getUniqueID())||stack==null)return;
        if(creative){if(player.capabilities.isCreativeMode){stack=null;setDead();}return;}
        if(player.inventory.addItemStackToInventory(stack)){stack=null;setDead();}
    }

	@Override public void onUpdate() {
		if (world.isRemote) { byte flags=dataManager.get(RETURNING); returning=(flags&1)!=0;embedded=(flags&2)!=0;if(returning||embedded){onEntityUpdate();return;} }
		if (embedded && !returning) {
            if(!world.isRemote && groundKnown && groundAir()){embedded=false;groundTicks=0;syncFlags();motionY=-.05;return;}
            onEntityUpdate();groundTicks++;if(groundTicks>4&&Tridents.level(Tridents.loyalty,stack)>0){returning=true;ItemSounds.play(this,"item.trident.return",10,1);syncFlags();noClip=true;}if(!world.isRemote&&groundTicks>=1200&&(creative||Tridents.level(Tridents.loyalty,stack)<=0))setDead();return; }
        if (world.isRemote || !returning) { super.onUpdate(); if(!returning&&!embedded&&isInWater()){motionX*=.99/.8;motionY=(motionY+.05)*(.99/.8)-.05;motionZ*=.99/.8;} return; }
		onEntityUpdate(); EntityLivingBase player=getThrower();
		if (player == null || !player.isEntityAlive() || player.dimension != dimension || player instanceof EntityPlayer&&((EntityPlayer)player).isSpectator()) { if (stack != null) { if(!creative)entityDropItem(stack, 0); stack = null; } setDead(); return; }
		double dx = player.posX - posX, dy = player.posY + player.getEyeHeight() - posY, dz = player.posZ - posZ, length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length < 1.5) { if(!(player instanceof EntityPlayer)){stack=null;setDead();return;} if(creative){stack=null;setDead();return;} if (stack != null && player instanceof EntityPlayer && ((EntityPlayer)player).inventory.addItemStackToInventory(stack)) { stack = null; setDead(); }  return; }
		int loyalty=Tridents.level(Tridents.loyalty,stack);posY+=dy*.015*loyalty;double speed=.05*loyalty;motionX=motionX*.95+dx/length*speed;motionY=motionY*.95+dy/length*speed;motionZ=motionZ*.95+dz/length*speed; setPosition(posX + motionX, posY + motionY, posZ + motionZ);
	}
	@Override public void writeEntityToNBT(NBTTagCompound tag) { super.writeEntityToNBT(tag); if (stack != null) tag.setTag("Trident", stack.writeToNBT(new NBTTagCompound())); tag.setInteger("GroundTicks",groundTicks);tag.setBoolean("GroundKnown",groundKnown);tag.setInteger("GroundX",groundX);tag.setInteger("GroundY",groundY);tag.setInteger("GroundZ",groundZ);tag.setBoolean("Returning", returning);tag.setBoolean("Embedded",embedded);tag.setBoolean("DealtDamage",dealtDamage);tag.setBoolean("Creative",creative); if (owner != null) tag.setString("OwnerUUID", owner.toString()); }
	@Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag); stack = tag.hasKey("Trident", 10) ? new ItemStack(tag.getCompoundTag("Trident")) : null; if (stack != null && !Tridents.known(stack)) stack = null; groundTicks=Math.max(0,tag.getInteger("GroundTicks"));groundKnown=tag.getBoolean("GroundKnown");groundX=tag.getInteger("GroundX");groundY=tag.getInteger("GroundY");groundZ=tag.getInteger("GroundZ");embedded=tag.getBoolean("Embedded");dealtDamage=tag.getBoolean("DealtDamage");creative=tag.getBoolean("Creative");returning = tag.getBoolean("Returning"); noClip = returning; syncFlags(); try { owner = java.util.UUID.fromString(tag.getString("OwnerUUID")); } catch (IllegalArgumentException ignored) { owner = null; } }

    private void rememberGround(int x,int y,int z){groundX=x;groundY=y;groundZ=z;groundKnown=true;groundTicks=0;}
    private boolean groundAir(){return world.isAirBlock(new net.minecraft.util.math.BlockPos(groundX,groundY,groundZ));}
    private void syncFlags(){dataManager.set(RETURNING, (byte) ((returning?1:0)|(embedded?2:0)));}
    /** Native pickup and rendering receive the actual thrown stack, including foil and custom data. */
    public boolean enchanted(){return stack!=null&&stack.isItemEnchanted();}
    @Override public EntityLivingBase getThrower(){
        if(resolvedOwner!=null&&resolvedOwner.world==world)return resolvedOwner;
        if(owner!=null){EntityPlayer player=world.getPlayerEntityByUUID(owner);if(player!=null)return resolvedOwner=player;
            for(Object object:world.loadedEntityList)if(object instanceof EntityLivingBase&&owner.equals(((EntityLivingBase)object).getUniqueID()))return resolvedOwner=(EntityLivingBase)object;
        }
        return super.getThrower();
    }
    @Override public void writeSpawnData(io.netty.buffer.ByteBuf data){net.minecraftforge.fml.common.network.ByteBufUtils.writeItemStack(data,stack==null?ItemStack.EMPTY:stack);}
    @Override public void readSpawnData(io.netty.buffer.ByteBuf data){stack=net.minecraftforge.fml.common.network.ByteBufUtils.readItemStack(data);}

    /** Native modern launch distribution/inertia, reusing the owned arrow launch calculation. */
    public void launch(EntityLivingBase shooter){
        net.minecraft.util.math.Vec3d look=shooter.getLookVec();
        motionX=(look.x+(rand.nextDouble()-rand.nextDouble())*.0172275)*2.5+shooter.motionX;
        motionY=(look.y+(rand.nextDouble()-rand.nextDouble())*.0172275)*2.5+(shooter.onGround?0:shooter.motionY);
        motionZ=(look.z+(rand.nextDouble()-rand.nextDouble())*.0172275)*2.5+shooter.motionZ;
        setPosition(shooter.posX,shooter.getEntityBoundingBox().minY+shooter.getEyeHeight()-.1,shooter.posZ);
        rotationYaw=prevRotationYaw=(float)(Math.atan2(motionX,motionZ)*180/Math.PI);rotationPitch=prevRotationPitch=(float)(Math.atan2(motionY,Math.sqrt(motionX*motionX+motionZ*motionZ))*180/Math.PI);
    }
}
