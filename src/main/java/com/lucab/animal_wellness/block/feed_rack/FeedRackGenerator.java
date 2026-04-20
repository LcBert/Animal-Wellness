package com.lucab.animal_wellness.block.feed_rack;

import com.lucab.animal_wellness.AnimalWellness;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class FeedRackGenerator extends BlockStateProvider {
    public FeedRackGenerator(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        createRack(AnimalWellness.OAK_FEED_RACK.get(), "oak");
        createRack(AnimalWellness.SPRUCE_FEED_RACK.get(), "spruce");
    }

    private void createRack(Block block, String woodName) {
        String baseName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ResourceLocation woodTexture = ResourceLocation.withDefaultNamespace("block/" + woodName + "_planks");

        ModelFile leftModel = models().withExistingParent(baseName + "_left", modLoc("block/feed_rack_left"))
                .texture("texture", woodTexture)
                .texture("particle", woodTexture);
        ModelFile rightModel = models().withExistingParent(baseName + "_right", modLoc("block/feed_rack_right"))
                .texture("texture", woodTexture)
                .texture("particle", woodTexture);

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(FeedRackBlock.FACING);
            RackPart part = state.getValue(FeedRackBlock.PART);
            ModelFile model = (part == RackPart.LEFT) ? leftModel : rightModel;

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) facing.getOpposite().toYRot()) // Ruota il modello in base al facing
                    .build();
        });
    }
}
