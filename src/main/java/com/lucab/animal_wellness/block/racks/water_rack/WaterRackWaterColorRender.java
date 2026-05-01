package com.lucab.animal_wellness.block.racks.water_rack;

import com.lucab.animal_wellness.AnimalWellness;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class WaterRackWaterColorRender {
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
                    if (tintIndex == 0) {
                        return 0x3F76E4;
                    }
                    return -1;
                },
                AnimalWellness.STONE_WATER_RACK.get(),
                AnimalWellness.COBBLESTONE_WATER_RACK.get(),
                AnimalWellness.STONE_BRICKS_WATER_RACK.get(),
                AnimalWellness.DEEPSLATE_WATER_RACK.get(),
                AnimalWellness.TUFF_WATER_RACK.get(),
                AnimalWellness.BLACKSTONE_WATER_RACK.get());
    }
}
