package com.lucab.animal_wellness.client;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.block.nest.NestBlockEntity;
import com.lucab.animal_wellness.block.nest.NestBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID, value = Dist.CLIENT)
public class AnimalWellnessClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(AnimalWellness.NEST_BLOCK_ENTITY.get(), NestBlockRenderer::new);
        });
    }
}
