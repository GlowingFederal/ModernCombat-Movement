package com.glowingfederal.moderncombatmovement.compat;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
/** Version-native sound dispatch for unchanged modern item assets. */
public final class ItemSounds {
 private ItemSounds(){}
  private static final java.util.Map<String,net.minecraft.util.SoundEvent> EVENTS=new java.util.HashMap<String,net.minecraft.util.SoundEvent>();
 public static void init(){
  try(java.io.InputStream stream=ItemSounds.class.getResourceAsStream("/assets/moderncombatmovement/sounds.json")){
   if(stream==null)throw new IllegalStateException("Missing item sound definitions");
   com.google.gson.JsonObject sounds=new com.google.gson.JsonParser().parse(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
   for(java.util.Map.Entry<String,com.google.gson.JsonElement> entry:sounds.entrySet()){
    net.minecraft.util.ResourceLocation key=new net.minecraft.util.ResourceLocation("moderncombatmovement",entry.getKey());net.minecraft.util.SoundEvent sound=new net.minecraft.util.SoundEvent(key).setRegistryName(key);
    net.minecraftforge.fml.common.registry.GameRegistry.findRegistry(net.minecraft.util.SoundEvent.class).register(sound);EVENTS.put(entry.getKey(),sound);
   }
  }catch(java.io.IOException failure){throw new IllegalStateException("Cannot read item sound definitions",failure);}
 }

 public static void play(Entity entity,String event,float volume,float pitch){if(!entity.world.isRemote){net.minecraft.util.SoundEvent sound=EVENTS.get(event);if(sound!=null)entity.world.playSound(null,entity.posX,entity.posY,entity.posZ,sound,entity.getSoundCategory(),volume,pitch);}}
 public static void at(World world,double x,double y,double z,String event,float volume,float pitch){if(!world.isRemote){net.minecraft.util.SoundEvent sound=EVENTS.get(event);if(sound!=null)world.playSound(null,x,y,z,sound,net.minecraft.util.SoundCategory.BLOCKS,volume,pitch);}}
}
