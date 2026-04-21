package com.lucab.animal_wellness.block.feed_rack;

import com.lucab.animal_wellness.AnimalWellness;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Map;

public class FeedRackItemGenerator extends ItemModelProvider {
    private final Map<Block, String> textures;

    public FeedRackItemGenerator(PackOutput output, String modId, ExistingFileHelper existingFileHelper, Map<Block, String> textures) {
        super(output, modId, existingFileHelper);
        this.textures = textures;
    }

    @Override
    protected void registerModels() {
        textures.forEach(this::createRackItem);
    }

    private void createRackItem(Block block, String woodName) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String[] texture = woodName.split(":");
        ResourceLocation woodTexture = ResourceLocation.fromNamespaceAndPath(texture[0], "block/" + texture[1]);

        withExistingParent(blockId.toString(), modLoc("block/feed_rack"))
                .texture("texture", woodTexture)
                .texture("particle", woodTexture);
    }
}
