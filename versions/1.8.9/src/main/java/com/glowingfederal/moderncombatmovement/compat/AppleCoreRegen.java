package com.glowingfederal.moderncombatmovement.compat;

import java.lang.reflect.Field;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.*;
import com.glowingfederal.moderncombatmovement.config.Settings;

/** Optional AppleCore event bridge. Its FoodStats update, cancellation, timers and synchronization stay authoritative. */
public final class AppleCoreRegen {
    private AppleCoreRegen() {}
    public static boolean present() {
        return net.minecraft.launchwrapper.Launch.classLoader.getResource(
                "squeek/applecore/api/hunger/HealthRegenEvent$AllowRegen.class")!=null;
    }
    public static void init() {
        if(!Loader.isModLoaded("AppleCore") || Loader.isModLoaded("modernregen"))return;
        try {
            // Forge 1.8.9 has no public bus-ID accessor. Register only these two API events,
            // without a hard API dependency or an expensive listener for every Forge event.
            Field bus=EventBus.class.getDeclaredField("busID");bus.setAccessible(true);
            int id=bus.getInt(MinecraftForge.EVENT_BUS);
            listen(id,"GetRegenTickPeriod",true);
            listen(id,"Regen",false);
        } catch(ReflectiveOperationException failure) {
            throw new IllegalStateException("AppleCore regeneration event API changed",failure);
        }
    }
    private static void listen(int bus,String name,boolean period) throws ReflectiveOperationException {
        Class<?> type=Class.forName("squeek.applecore.api.hunger.HealthRegenEvent$"+name);
        Event prototype=(Event)type.getConstructor(EntityPlayer.class).newInstance((Object)null);
        final Field player=type.getField("player");
        final Field value=type.getField(period?"regenTickPeriod":"deltaHealth");
        final Field exhaustion=period?null:type.getField("deltaExhaustion");
        prototype.getListenerList().register(bus,EventPriority.HIGHEST,event->{
            if(event.isCancelable()&&event.isCanceled())return;
            try {
                EntityPlayer p=(EntityPlayer)player.get(event);
                if(p==null||!Settings.combat(p.worldObj.isRemote))return;
                FoodStats food=p.getFoodStats();boolean saturated=food.getFoodLevel()>=20&&food.getSaturationLevel()>0;
                if(period)value.setInt(event,saturated?10:80);
                else {
                    float budget=saturated?Math.min(food.getSaturationLevel(),6):6;
                    value.setFloat(event,saturated?budget/6:1);
                    exhaustion.setFloat(event,budget);
                }
            } catch(IllegalAccessException failure) { throw new IllegalStateException("AppleCore event access failed",failure); }
        });
    }
}
