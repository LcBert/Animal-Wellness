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

import java.util.Map;

public class FeedRackGenerator extends BlockStateProvider {
    private final Map<Block, String> textures;

    public FeedRackGenerator(PackOutput output, String modId, ExistingFileHelper existingFileHelper, Map<Block, String> textures) {
        super(output, modId, existingFileHelper);
        this.textures = textures;
    }

    @Override
    protected void registerStatesAndModels() {
        textures.forEach(this::createRack);
//        createRack(AnimalWellness.OAK_FEED_RACK.get(), "minecraft:stone");
//        createRack(AnimalWellness.SPRUCE_FEED_RACK.get(), "minecraft:diorite");
    }

    private void createRack(Block block, String woodName) {
        String baseName = BuiltInRegistries.BLOCK.getKey(block).getPath();
//        ResourceLocation woodTexture = ResourceLocation.withDefaultNamespace("block/" + woodName + "_planks");
        String[] texture = woodName.split(":");
        ResourceLocation woodTexture = ResourceLocation.fromNamespaceAndPath(texture[0], "block/" + texture[1]);

        ModelFile model = models().withExistingParent(baseName, modLoc("block/feed_rack"))
                .texture("texture", woodTexture)
                .texture("particle", woodTexture);

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(FeedRackBlock.FACING);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) facing.getOpposite().toYRot())
                    .build();
        });
    }
}
