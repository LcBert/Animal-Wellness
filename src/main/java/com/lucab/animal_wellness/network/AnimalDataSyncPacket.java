package com.lucab.animal_wellness.network;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.lucab.animal_wellness.AnimalWellness.MODID;

public record AnimalDataSyncPacket(int entityId, CompoundTag attachmentNbt) implements CustomPacketPayload {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "animal_data_sync_packet");
    public static final Type<AnimalDataSyncPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, AnimalDataSyncPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.entityId);
                buf.writeNbt(payload.attachmentNbt);
            },
            buf -> new AnimalDataSyncPacket(buf.readVarInt(), buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AnimalDataSyncPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            Entity entity = minecraft.level.getEntity(data.entityId);
            if (entity != null) {
                WellnessAttachment attachment = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
                attachment.deserializeNBT(minecraft.level.registryAccess(), data.attachmentNbt);
            }
        });
    }
}
