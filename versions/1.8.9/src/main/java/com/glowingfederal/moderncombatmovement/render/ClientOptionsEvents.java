package com.glowingfederal.moderncombatmovement.render;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
public final class ClientOptionsEvents {
 @SubscribeEvent public void changed(net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent e){if(e.modID.equals(com.glowingfederal.moderncombatmovement.Identity.MOD_ID)){com.glowingfederal.moderncombatmovement.config.ClientOptions.load(com.glowingfederal.moderncombatmovement.config.Settings.configuration);com.glowingfederal.moderncombatmovement.config.Settings.configuration.save();}}
}
