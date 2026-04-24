package com.lucab.animal_wellness.network;

import com.lucab.animal_wellness.screen.AnimalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.lucab.animal_wellness.AnimalWellness.MODID;

public record OpenAnimalScreenPacket(int entityId) implements CustomPacketPayload {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "open_animal_screen_packet");
    public static final Type<OpenAnimalScreenPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, OpenAnimalScreenPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.entityId);
            },
            buf -> new OpenAnimalScreenPacket(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenAnimalScreenPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Entity entity = minecraft.level.getEntity(data.entityId);
            if (entity != null) {
                Screen screen = new AnimalScreen(entity);
                minecraft.setScreen(screen);
            }
        });
    }
}
