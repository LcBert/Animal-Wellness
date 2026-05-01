package com.lucab.animal_wellness;

import com.lucab.animal_wellness.attachments.WellnessAttachment;
import com.lucab.animal_wellness.block.nest.NestBlock;
import com.lucab.animal_wellness.block.nest.NestBlockEntity;
import com.lucab.animal_wellness.block.manure.ManureBlock;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlock;
import com.lucab.animal_wellness.block.racks.feed_rack.FeedRackBlockEntity;
import com.lucab.animal_wellness.block.racks.water_rack.WaterRackBlock;
import com.lucab.animal_wellness.block.racks.water_rack.WaterRackBlockEntity;
import com.lucab.animal_wellness.block.manures_farmland.ManuredFarmland;
import com.lucab.animal_wellness.command.WellnessCommand;
import com.lucab.animal_wellness.item.AnimalInspector;
import com.lucab.animal_wellness.item.AnimalBrush;
import com.lucab.animal_wellness.network.AnimalDataSyncPacket;
import com.lucab.animal_wellness.network.AnimalDataSyncRequestPacket;
import com.lucab.animal_wellness.network.OpenAnimalScreenPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLY_PARTICLE = PARTICLE_TYPES.register("fly_particle", () -> new SimpleParticleType(true));

    // Animal Attachment
    public static final Supplier<AttachmentType<WellnessAttachment>> ANIMAL_WELLNESS_ATTACHMENT = ATTACHMENT_TYPE.register(
            "animal_wellness", () -> AttachmentType.serializable(WellnessAttachment::new).build());

    // Animal Food
    public static final DeferredItem<Item> ANIMAL_FOOD = ITEMS.register("animal_food", () -> new Item(new Item.Properties()));

    // Brush
    public static final DeferredItem<Item> ANIMAL_BRUSH = ITEMS.register("animal_brush", AnimalBrush::new);

    // Animal Inspector
    public static final DeferredItem<Item> ANIMAL_INSPECTOR = ITEMS.register("animal_inspector", AnimalInspector::new);

    // Manure Block
    public static final DeferredBlock<Block> MANURE_BLOCK = BLOCKS.register("manure", ManureBlock::new);
    public static final DeferredItem<BlockItem> MANURE = ITEMS.register("manure", () -> new BlockItem(MANURE_BLOCK.get(), new Item.Properties()));

    // Manured Farmland
    public static final DeferredBlock<Block> MANURED_FARMLAND = BLOCKS.register("manured_farmland", ManuredFarmland::new);
    public static final DeferredItem<BlockItem> MANURED_FARMLAND_ITEM = ITEMS.register("manured_farmland", () -> new BlockItem(MANURED_FARMLAND.get(), new Item.Properties()));

    // Nest
    public static final DeferredBlock<Block> NEST_BLOCK = BLOCKS.register("nest", () -> new NestBlock());
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NestBlockEntity>> NEST_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("nest", () -> BlockEntityType.Builder.of(NestBlockEntity::new, NEST_BLOCK.get()).build(null));
    public static final DeferredItem<BlockItem> NEST_ITEM = ITEMS.register("nest", () -> new BlockItem(NEST_BLOCK.get(), new Item.Properties()));

    // Feed rack
    public static final DeferredBlock<Block> OAK_FEED_RACK = BLOCKS.register("oak_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> OAK_FEED_RACK_ITEM = ITEMS.register("oak_feed_rack", () -> new BlockItem(OAK_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> SPRUCE_FEED_RACK = BLOCKS.register("spruce_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> SPRUCE_FEED_RACK_ITEM = ITEMS.register("spruce_feed_rack", () -> new BlockItem(SPRUCE_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> BIRCH_FEED_RACK = BLOCKS.register("birch_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> BIRCH_FEED_RACK_ITEM = ITEMS.register("birch_feed_rack", () -> new BlockItem(BIRCH_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> JUNGLE_FEED_RACK = BLOCKS.register("jungle_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> JUNGLE_FEED_RACK_ITEM = ITEMS.register("jungle_feed_rack", () -> new BlockItem(JUNGLE_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> ACACIA_FEED_RACK = BLOCKS.register("acacia_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> ACACIA_FEED_RACK_ITEM = ITEMS.register("acacia_feed_rack", () -> new BlockItem(ACACIA_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> DARK_OAK_FEED_RACK = BLOCKS.register("dark_oak_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> DARK_OAK_FEED_RACK_ITEM = ITEMS.register("dark_oak_feed_rack", () -> new BlockItem(DARK_OAK_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> CRIMSON_FEED_RACK = BLOCKS.register("crimson_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> CRIMSON_FEED_RACK_ITEM = ITEMS.register("crimson_feed_rack", () -> new BlockItem(CRIMSON_FEED_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> WARPED_FEED_RACK = BLOCKS.register("warped_feed_rack", () -> new FeedRackBlock());
    public static final DeferredItem<BlockItem> WARPED_FEED_RACK_ITEM = ITEMS.register("warped_feed_rack", () -> new BlockItem(WARPED_FEED_RACK.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeedRackBlockEntity>> FEED_RACK_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("oak_feed_rack", () -> BlockEntityType.Builder.of(FeedRackBlockEntity::new,
                    OAK_FEED_RACK.get(),
                    SPRUCE_FEED_RACK.get(),
                    BIRCH_FEED_RACK.get(),
                    JUNGLE_FEED_RACK.get(),
                    ACACIA_FEED_RACK.get(),
                    DARK_OAK_FEED_RACK.get(),
                    CRIMSON_FEED_RACK.get(),
                    WARPED_FEED_RACK.get()
            ).build(null));

    // Water rack
    public static final DeferredBlock<Block> STONE_WATER_RACK = BLOCKS.register("stone_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> STONE_WATER_RACK_ITEM = ITEMS.register("stone_water_rack", () -> new BlockItem(STONE_WATER_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> COBBLESTONE_WATER_RACK = BLOCKS.register("cobblestone_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> COBBLESTONE_WATER_RACK_ITEM = ITEMS.register("cobblestone_water_rack", () -> new BlockItem(COBBLESTONE_WATER_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> STONE_BRICKS_WATER_RACK = BLOCKS.register("stone_bricks_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> STONE_BRICKS_WATER_RACK_ITEM = ITEMS.register("stone_bricks_water_rack", () -> new BlockItem(STONE_BRICKS_WATER_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> DEEPSLATE_WATER_RACK = BLOCKS.register("deepslate_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> DEEPSLATE_WATER_RACK_ITEM = ITEMS.register("deepslate_water_rack", () -> new BlockItem(DEEPSLATE_WATER_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> TUFF_WATER_RACK = BLOCKS.register("tuff_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> TUFF_WATER_RACK_ITEM = ITEMS.register("tuff_water_rack", () -> new BlockItem(TUFF_WATER_RACK.get(), new Item.Properties()));
    public static final DeferredBlock<Block> BLACKSTONE_WATER_RACK = BLOCKS.register("blackstone_water_rack", () -> new WaterRackBlock());
    public static final DeferredItem<BlockItem> BLACKSTONE_WATER_RACK_ITEM = ITEMS.register("blackstone_water_rack", () -> new BlockItem(BLACKSTONE_WATER_RACK.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterRackBlockEntity>> WATER_RACK_BLOCK_ENTITY = BLOCK_ENTITIES
            .register("water_rack", () -> BlockEntityType.Builder.of(WaterRackBlockEntity::new,
                    STONE_WATER_RACK.get(),
                    COBBLESTONE_WATER_RACK.get(),
                    STONE_BRICKS_WATER_RACK.get(),
                    DEEPSLATE_WATER_RACK.get(),
                    TUFF_WATER_RACK.get(),
                    BLACKSTONE_WATER_RACK.get()
            ).build(null));

    public AnimalWellness(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ATTACHMENT_TYPE.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloads);

        addCreativeTab();
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FEED_RACK_BLOCK_ENTITY.get(),
                (be, side) -> {
                    FeedRackBlockEntity rack = FeedRackBlockEntity.getFeedRack(be.getLevel(), be.getBlockPos(), be.getBlockState());
                    return rack != null ? rack.inventory : be.inventory;
                }
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                WATER_RACK_BLOCK_ENTITY.get(),
                (be, side) -> {
                    WaterRackBlockEntity rack = WaterRackBlockEntity.getWaterRack(be.getLevel(), be.getBlockPos(), be.getBlockState());
                    return rack != null ? rack.tank : be.tank;
                }
        );
    }

    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(OpenAnimalScreenPacket.TYPE, OpenAnimalScreenPacket.STREAM_CODEC, OpenAnimalScreenPacket::handle);
        registrar.playToClient(AnimalDataSyncPacket.TYPE, AnimalDataSyncPacket.STREAM_CODEC, AnimalDataSyncPacket::handle);
        registrar.playToServer(AnimalDataSyncRequestPacket.TYPE, AnimalDataSyncRequestPacket.STREAM_CODEC, AnimalDataSyncRequestPacket::handle);
    }

    public void addCreativeTab() {
        CREATIVE_MODE_TABS.register("animal_wellness_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.animal_wellness"))
                .icon(() -> ANIMAL_FOOD.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ANIMAL_FOOD);
                    output.accept(ANIMAL_BRUSH);
                    output.accept(ANIMAL_INSPECTOR);
                    output.accept(MANURE);
                    output.accept(MANURED_FARMLAND_ITEM);
                    output.accept(NEST_ITEM);
                    output.accept(OAK_FEED_RACK_ITEM);
                    output.accept(SPRUCE_FEED_RACK_ITEM);
                    output.accept(BIRCH_FEED_RACK_ITEM);
                    output.accept(JUNGLE_FEED_RACK_ITEM);
                    output.accept(ACACIA_FEED_RACK_ITEM);
                    output.accept(DARK_OAK_FEED_RACK_ITEM);
                    output.accept(CRIMSON_FEED_RACK_ITEM);
                    output.accept(WARPED_FEED_RACK_ITEM);
                    output.accept(STONE_WATER_RACK_ITEM);
                    output.accept(COBBLESTONE_WATER_RACK_ITEM);
                    output.accept(STONE_BRICKS_WATER_RACK_ITEM);
                    output.accept(DEEPSLATE_WATER_RACK_ITEM);
                    output.accept(TUFF_WATER_RACK_ITEM);
                    output.accept(BLACKSTONE_WATER_RACK_ITEM);
                }).build());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        WellnessCommand.register(event.getDispatcher());
    }
}
