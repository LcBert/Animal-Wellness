package com.lucab.animal_wellness;

import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.feed_rack.FeedRackBlockEntity;
import com.lucab.animal_wellness.config.WellnessConfig;
import com.lucab.animal_wellness.item.AnimalInspector;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(com.lucab.animal_wellness.AnimalWellness.MODID)
public class AnimalWellness {
    public static final String MODID = "animal_wellness";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    // Animal Feed
    public static final DeferredItem<Item> ANIMAL_FEED = ITEMS.register("animal_feed", () -> new Item(new Item.Properties()));

    // Animal Inspector
    public static final DeferredItem<Item> ANIMAL_INSPECTOR = ITEMS.register("animal_inspector", AnimalInspector::new);

    // Feed rack
    public static final DeferredBlock<Block> OAK_FEED_RACK = BLOCKS.register("oak_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> OAK_FEED_RACK_ITEM = ITEMS.register("oak_feed_rack", () -> new BlockItem(OAK_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> SPRUCE_FEED_RACK = BLOCKS.register("spruce_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> SPRUCE_FEED_RACK_ITEM = ITEMS.register("spruce_feed_rack", () -> new BlockItem(SPRUCE_FEED_RACK.get(), new Item.Properties()));

    // Animal Attachment
    public static final Supplier<AttachmentType<WellnessAttachment>> ANIMAL_WELLNESS_ATTACHMENT = ATTACHMENT_TYPE.register(
            "animal_wellness", () -> AttachmentType.serializable(WellnessAttachment::new).build());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeedRackBlockEntity>> FEED_RACK_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("oak_feed_rack", () -> BlockEntityType.Builder.of(FeedRackBlockEntity::new,
                    OAK_FEED_RACK.get(),
                    SPRUCE_FEED_RACK.get()
            ).build(null));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ANIMAL_WELLNESS_TAB = CREATIVE_MODE_TABS
            .register("animal_wellness_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.animal_wellness"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.WHEAT::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        output.accept(ANIMAL_FEED);
                        output.accept(ANIMAL_INSPECTOR);
                        output.accept(OAK_FEED_RACK_ITEM);
                        output.accept(SPRUCE_FEED_RACK_ITEM);
                    }).build());

    public AnimalWellness(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ATTACHMENT_TYPE.register(modEventBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        WellnessConfig.load();
    }
}
