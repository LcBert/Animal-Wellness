package com.lucab.animal_wellness;

import com.lucab.animal_wellness.block.feed_rack.FeedRackGenerator;
import com.lucab.animal_wellness.block.feed_rack.FeedRackItemGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = AnimalWellness.MODID)
public class DataEvents {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        Map<Block, String> textures = new HashMap<>();
        textures.put(AnimalWellness.OAK_FEED_RACK.get(), "minecraft:oak_planks");
        textures.put(AnimalWellness.SPRUCE_FEED_RACK.get(), "minecraft:spruce_planks");

        generator.addProvider(event.includeClient(), new FeedRackGenerator(packOutput, AnimalWellness.MODID, existingFileHelper, textures));
        generator.addProvider(event.includeClient(), new FeedRackItemGenerator(packOutput, AnimalWellness.MODID, existingFileHelper, textures));
    }
}
