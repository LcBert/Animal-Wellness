package com.lucab.animal_wellness.network;

import com.lucab.animal_wellness.AnimalWellness;
import com.lucab.animal_wellness.attachments.WellnessAttachment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.lucab.animal_wellness.AnimalWellness.MODID;

public record AnimalDataSyncRequestPacket(int entityId) implements CustomPacketPayload {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "animal_data_sync_request_packet");
    public static final Type<AnimalDataSyncRequestPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, AnimalDataSyncRequestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.entityId);
            },
            buf -> new AnimalDataSyncRequestPacket(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AnimalDataSyncRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Entity entity = serverPlayer.serverLevel().getEntity(packet.entityId());
                if (entity != null) {
                    WellnessAttachment attachment = entity.getData(AnimalWellness.ANIMAL_WELLNESS_ATTACHMENT.get());
                    PacketDistributor.sendToPlayer(serverPlayer, new AnimalDataSyncPacket(packet.entityId(), attachment.serializeNBT(entity.level().registryAccess())));
                }
            }
        });
    }
}
