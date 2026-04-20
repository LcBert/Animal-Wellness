package com.lucab.animal_wellness.config;

import com.lucab.animal_wellness.AnimalWellness;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class ConfigReload {
    @SubscribeEvent
    public static void onConfigReload(AddReloadListenerEvent event) {
        WellnessConfig.load();
    }
}
